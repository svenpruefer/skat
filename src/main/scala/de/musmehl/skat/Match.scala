// Copyright 2018 Sven Prüfer
//
// This file is part of skat.
//
// skat is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// skat is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with skat.  If not, see <http://www.gnu.org/licenses/>.

package de.musmehl.skat

import scala.annotation.tailrec
import scala.collection.immutable.Queue

trait MatchBase {

    def id: String

    def players: List[PlayerBase]

    def dealCards: MatchState

    def reizen(matchState: MatchState): MatchState

    def playRound(matchState: MatchState): MatchState

    def playFullGame: Map[PlayerBase, Int]

}

case class Match(id: String, players: List[PlayerBase]) extends MatchBase {

    require(players.length == 3, "Skat can only be played by exactly three players")

    override def dealCards: MatchState = {
        val shuffledCards = scala.util.Random.shuffle(Card.allCards)

        val noDealtCards = Map[PlayerBase, Set[Card]](
            (players.head, Set.empty[Card]),
            (players(1), Set.empty[Card]),
            (players(2), Set.empty[Card])
        )

        val (dealtCards, skat) = shuffledCards.foldLeft((noDealtCards, Set.empty[Card]))((agg, el) => {
            shuffledCards.indexOf(el) match {
                case i: Int if Set(0, 1, 2, 11, 12, 13, 14, 23, 24, 25).contains(i) =>
                    (agg._1.updated(players.head, agg._1(players.head) + el), agg._2)
                case i: Int if Set(3, 4, 5, 15, 16, 17, 18, 26, 27, 28).contains(i) =>
                    (agg._1.updated(players(1), agg._1(players(1)) + el), agg._2)
                case i: Int if Set(6, 7, 8, 19, 20, 21, 22, 29, 30, 31).contains(i) =>
                    (agg._1.updated(players(2), agg._1(players(2)) + el), agg._2)
                case i: Int if Set(9, 10).contains(i) => (agg._1, agg._2 + el)
            }
        })

        MatchState(
            Map[PlayerBase, PlayerState](
                (players.head, PlayerState(dealtCards(players.head), Set.empty[Stich], weg = false)),
                (players(1), PlayerState(dealtCards(players(1)), Set.empty[Stich], weg = false)),
                (players(2), PlayerState(dealtCards(players(2)), Set.empty[Stich], weg = false))
            ),
            Queue(players: _*),
            skat,
            None,
            None
        )
    }

    override def reizen(matchState: MatchState): MatchState = {
        require(matchState.game.isEmpty, "It is not possible to do a 'Reizen' step when there is already a game.")
        require(matchState.singlePlayer.isEmpty, "It is not possible to do a 'Reizen' step when there is already a single player.")

        @tailrec
        def reizTwoPlayers(sager: PlayerBase, hörer: PlayerBase, biggerThan: Int = 0): (PlayerBase, Int) = {
            val newReizValue = sager.sagt(biggerThan)
            if (newReizValue.isEmpty) {
                (hörer, biggerThan)
            } else if (hörer.hört(newReizValue.get)) {
                reizTwoPlayers(sager, hörer, newReizValue.get)
            } else (sager, newReizValue.get)
        }

        val (hörer, rest) = matchState.order.dequeue
        val (sager, weiterSager) = (rest.dequeue._1, rest.dequeue._2.front)

        val (firstWinner, newValue) = reizTwoPlayers(sager, hörer)

        val (actualWinner, finalValue) = reizTwoPlayers(weiterSager, firstWinner, newValue)

        val reizResults = Map[PlayerBase, Option[Int]](
            (actualWinner, Some(finalValue)),
            (firstWinner, Some(finalValue - 1)),
            (matchState.playerStates.keySet.find(p => p != actualWinner && p != firstWinner).head, Some(newValue - 1))
        ) // TODO fix these values

        val actualWinnerState = matchState.playerStates(actualWinner)
        val hand = actualWinner.decidesHandGame(reizResults, matchState.order, actualWinnerState.cards)

        val (game, newSkat) = if (hand) actualWinner.decidesForGame(reizResults, matchState.order, actualWinnerState.cards, None)
        else actualWinner.decidesForGame(reizResults, matchState.order, actualWinnerState.cards, Some(matchState.skat))

        val newSinglePlayerState = actualWinnerState.copy(
            cards  = actualWinnerState.cards.union(matchState.skat) -- newSkat.get.cards,
            stiche = Set(newSkat.get)
        )

        MatchState(
            matchState.playerStates.updated(actualWinner, newSinglePlayerState),
            matchState.order,
            Set.empty[Card],
            Some(game),
            Some(actualWinner)
        )
    }

    override def playRound(matchState: MatchState): MatchState = {
        require(matchState.game.isDefined, "A round can only be played if there is a game chosen")
        require(matchState.singlePlayer.isDefined, "A round can only be played if there is a single player chosen")
        require(matchState.order.length == 3, "A round can only be played if there are three players involved")
        require(matchState.skat.isEmpty, "A round can only be played if the Skat was already given to the single player")

        val playedCards = Map.empty[PlayerBase, Card]

        val (firstPlayer, remainingPlayers) = matchState.order.dequeue
        val afterFirstPlayer = playedCards +
            (firstPlayer -> firstPlayer.plays(matchState.playerStates(firstPlayer), matchState.game.get, Set.empty[Card]))

        val (secondPlayer, lastPlayers) = remainingPlayers.dequeue
        val afterSecondPlayer = afterFirstPlayer +
            (secondPlayer -> secondPlayer.plays(matchState.playerStates(secondPlayer), matchState.game.get, afterFirstPlayer.values.toSet))

        val thirdPlayer = lastPlayers.last
        val totalStich = afterSecondPlayer +
            (thirdPlayer -> thirdPlayer.plays(matchState.playerStates(thirdPlayer), matchState.game.get, afterSecondPlayer.values.toSet))

        val winner = matchState.game.get.determineWinnerOfStich(firstPlayer, totalStich)

        val indexOfWinner = matchState.order.indexOf(winner)
        val (firstPart, secondPart) = matchState.order.splitAt(indexOfWinner)
        val newOrder = secondPart.enqueue(firstPart)

        MatchState(
            Map[PlayerBase, PlayerState](
                (players.head, matchState.playerStates(players.head).playCard(totalStich(players.head))),
                (players(1), matchState.playerStates(players(1)).playCard(totalStich(players(1)))),
                (players(2), matchState.playerStates(players(2)).playCard(totalStich(players(2))))
            ),
            newOrder,
            Set.empty[Card],
            matchState.game,
            matchState.singlePlayer
        )
    }

    override def playFullGame: Map[PlayerBase, Int] = ???
}

case class MatchState(
    playerStates: Map[PlayerBase, PlayerState],
    order:        Queue[PlayerBase],
    skat:         Set[Card],
    game:         Option[Game],
    singlePlayer: Option[PlayerBase]
)
