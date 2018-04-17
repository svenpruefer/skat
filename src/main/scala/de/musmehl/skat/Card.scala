package de.musmehl.skat

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
  * @param color its color
  * @param value its value,
  */
case class Card(color: Color, value: Value) {
    val points: Int = value.points
}

case class Stich(card1: Card, card2: Card, card3: Card) {
    val totalValue: Int = card1.points + card2.points + card3.points
}
