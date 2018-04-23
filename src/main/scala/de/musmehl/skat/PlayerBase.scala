package de.musmehl.skat

trait PlayerBase {

    def name: String

    def totalPoints: Int

    def sagt(reizValue: Option[Int]): Option[Int]

    def hört(reizValue: Int): Boolean
}

case class Player(name: String,
                  totalPoints: Int) extends PlayerBase

case class PlayerState(cards: Set[Card], stiche: Set[Stich], weg: Boolean)
