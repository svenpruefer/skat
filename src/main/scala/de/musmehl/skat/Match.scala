package de.musmehl.skat

import scala.collection.immutable.Queue

trait MatchBase {

    def id: String

    def players: List[Player]

    def dealCards: MatchState

    def reizen(matchState: MatchState): MatchState

    def playRound(matchState: MatchState): MatchState

    def playFullGame: Map[Player, Int]

}

case class Match(id: String, players: List[Player]) extends MatchBase {

    require(players.length == 3, "Skat can only be played by exactly three players")

    override def dealCards: MatchState = {
        val shuffledCards = scala.util.Random.shuffle(Card.allCards)

        val noDealtCards = Map[Player, Set[Card]](
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
            Map[Player, PlayerState](
                (players.head, PlayerState(dealtCards(players.head), Set.empty[Stich], weg = false)),
                (players(1), PlayerState(dealtCards(players(1)), Set.empty[Stich], weg = false)),
                (players(2), PlayerState(dealtCards(players(2)), Set.empty[Stich], weg = false))
            ),
            players,
            skat,
            None,
            None
        )
    }

    override def reizen(matchState: MatchState): MatchState = ???

    override def playRound(matchState: MatchState): MatchState = {
        require(matchState.game.isDefined, "A round can only be played if there is a game chosen")
        require(matchState.singlePlayer.isDefined, "A round can only be played if there is a single player chosen")
        require(matchState.order.length == 3, "A round can only be played if there are three players involved")
        require(matchState.skat.isEmpty, "A round can only be played if the Skat was already given to the single player")

        val playedCards = Map.empty[Player, Card]

        val (firstPlayer, remainingPlayers) = matchState.order.dequeue
        val afterFirstPlayer = playedCards +
            (firstPlayer -> firstPlayer.plays(matchState.playerStates(firstPlayer), matchState.game.get, Set.empty[Card]))

        val (secondPlayer, lastPlayers) = remainingPlayers.dequeue
        val afterSecondPlayer = afterFirstPlayer +
            (secondPlayer -> secondPlayer.plays(matchState.playerStates(secondPlayer), matchState.game.get, afterFirstPlayer.values.toSet))

        val thirdPlayer = lastPlayers.last
        val totalStich = afterSecondPlayer +
            (thirdPlayer -> thirdPlayer.plays(matchState.playerStates(thirdPlayer), matchState.game.get, afterSecondPlayer.values.toSet))

        val winner = matchState.game.get.determineWinnerOfStich(totalStich)

        MatchState(
            Map[Player, PlayerState](
                (players.head, matchState.playerStates(players.head).playCard(totalStich(players.head))),
                (players(1), matchState.playerStates(players(1)).playCard(totalStich(players(1)))),
                (players(2), matchState.playerStates(players(2)).playCard(totalStich(players(2))))
            ),
            ,
            Set.empty[Card],
            matchState.game,
            matchState.singlePlayer
        )
    }

    override def playFullGame: Map[Player, Int] = ???
}

case class MatchState(playerStates: Map[Player, PlayerState],
                      order: Queue[Player],
                      skat: Set[Card],
                      game: Option[Game],
                      singlePlayer: Option[Player])
