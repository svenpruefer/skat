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

// region Colors

sealed trait Color {
    val colorValue: Int
}

case object Schell extends Color {
    val colorValue = 9
}

case object Rot extends Color {
    val colorValue = 10
}

case object Grün extends Color {
    val colorValue = 11
}

case object Eichel extends Color {
    val colorValue = 12
}

// endregion

// region Values

sealed trait Value {
    val points: Int
}

case object Sieben extends Value {
    val points = 0
}

case object Acht extends Value {
    val points = 0
}

case object Neun extends Value {
    val points = 0
}

case object Unter extends Value {
    val points = 2
}

case object Ober extends Value {
    val points = 3
}

case object König extends Value {
    val points = 4
}

case object Zehn extends Value {
    val points = 10
}

case object Ass extends Value {
    val points = 11
}

// endregion

/**
 * A card that can be played in Skat
 *
 * @param color its color
 * @param value its value,
 */
case class Card(color: Color, value: Value) {
    val points: Int = value.points
}

case class Stich(cards: Set[Card]) {
    require(cards.size <= 3, "A Stich cannot consist of more than three cards")
    val totalValue: Int = cards.map(_.value.points).sum

    def addCard(card: Card): Stich = copy(cards = this.cards + card)

}

object Card {

    val allCards: Queue[Card] = Queue(
        Card(Schell, Sieben),
        Card(Schell, Acht),
        Card(Schell, Neun),
        Card(Schell, Unter),
        Card(Schell, Ober),
        Card(Schell, König),
        Card(Schell, Zehn),
        Card(Schell, Ass),
        Card(Rot, Sieben),
        Card(Rot, Acht),
        Card(Rot, Neun),
        Card(Rot, Unter),
        Card(Rot, Ober),
        Card(Rot, König),
        Card(Rot, Zehn),
        Card(Rot, Ass),
        Card(Grün, Sieben),
        Card(Grün, Acht),
        Card(Grün, Neun),
        Card(Grün, Unter),
        Card(Grün, Ober),
        Card(Grün, König),
        Card(Grün, Zehn),
        Card(Grün, Ass),
        Card(Eichel, Sieben),
        Card(Eichel, Acht),
        Card(Eichel, Neun),
        Card(Eichel, Unter),
        Card(Eichel, Ober),
        Card(Eichel, König),
        Card(Eichel, Zehn),
        Card(Eichel, Ass)
    )
}
