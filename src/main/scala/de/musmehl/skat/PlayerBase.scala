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

import scala.collection.immutable.Queue

trait PlayerBase {

    def name: String

    def totalPoints: Int

    def sagt(reizValue: Int): Option[Int]

    def hört(reizValue: Int): Boolean

    def plays(playerState: PlayerState, game: Game, onTable: Set[Card]): Card

    def decidesHandGame(
        reizResults: Map[PlayerBase, Option[Int]],
        order:       Queue[PlayerBase],
        ownCards:    Set[Card]
    ): Boolean

    def decidesForGame(
        reizResults: Map[PlayerBase, Option[Int]],
        order:       Queue[PlayerBase],
        ownCards:    Set[Card],
        skat:        Option[Set[Card]]
    ): (Game, Option[Stich])

}

case class Player(
    name:        String,
    totalPoints: Int
) extends PlayerBase {

    override def sagt(reizValue: Int): Option[Int] = ???

    override def hört(reizValue: Int): Boolean = ???

    override def plays(playerState: PlayerState, game: Game, onTable: Set[Card]): Card = ???

    override def decidesForGame(
        reizResults: Map[PlayerBase, Option[Int]],
        order:       Queue[PlayerBase],
        ownCards:    Set[Card],
        skat:        Option[Set[Card]]
    ): (Game, Option[Stich]) = ???

    override def decidesHandGame(
        reizResults: Map[PlayerBase, Option[Int]],
        order:       Queue[PlayerBase],
        ownCards:    Set[Card]
    ): Boolean = ???
}

case class PlayerState(cards: Set[Card], stiche: Set[Stich], weg: Boolean) {

    def playCard(card: Card): PlayerState = {
        require(cards.contains(card), "In order to play a card, this card must be in possession of the player")
        copy(cards = this.cards - card)
    }
}
