package de.musmehl.skat

// region Colors

sealed trait Color

case object Schell extends Color

case object Rot extends Color

case object Grün extends Color

case object Eichel extends Color

// endregion

// region Values

sealed trait Value

case object Sieben extends Value

case object Acht extends Value

case object Neun extends Value

case object Unter extends Value

case object Ober extends Value

case object König extends Value

case object Zehn extends Value

case object Ass extends Value

// endregion

/**
  * A card that can be played in Skat
  * @param color its color
  * @param value its value,
  */
case class Card(color: Color, value: Value)
