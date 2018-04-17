package de.musmehl.skat

sealed trait Game {

    implicit def orderOfValues: Ordering[Value]

    def isTrump: Card => Boolean

    def pointValues: Card => Option[Int] = (card: Card) => {
        card match {
            case Card(_, Sieben) || Card(_, Acht) || Card(_, Neun) => Some(0)
            case Card(_, Unter) => Some(2)
            case Card(_, Ober) => Some(3)
            case Card(_, König) => Some(4)
            case Card(_, Zehn) => Some(10)
            case Card(_, Ass) => Some(11)
        }
    }

    def schneider: Boolean

    def angesagt: Boolean

    def schwarz: Boolean

    def ouvert: Boolean

    def hand: Boolean

    def totalPoints: Set[Card] => Int

    def leadingTrumps: Set[Card] => Option[Int]

}

/**
  * Rules and methods for Null game
  */
case class Null(hand: Boolean, ouvert: Boolean) extends Game {

    override implicit def orderOfValues: Ordering[Value] = nullOrdering

    override def pointValues: Card => Option[Int] = x => None

    override def schneider: Boolean = false

    override def angesagt: Boolean = false

    override def schwarz: Boolean = false

    override def totalPoints: Set[Card] => Int = (cards: Set[Card]) => {
        (hand, ouvert) match {
            case (true, false) => 35
            case (true, true) => 56
            case (false, true) => 45
            case (false, false) => 23
        }
    }

    override def isTrump: Card => Boolean = x => false

    override def leadingTrumps: Set[Card] => Option[Int] = (s: Set[Card]) => None
}

case class ColorGame(trump: Color,
                     hand: Boolean,
                     schneider: Boolean,
                     angesagt: Boolean,
                     schwarz: Boolean,
                     ouvert: Boolean) extends Game {

    override implicit def orderOfValues: Ordering[Value] = normalOrdering

    override def isTrump: Card => Boolean = (card: Card) => card.color == trump || card.value == Unter

    override def totalPoints: Set[Card] => Int = ???

    override def leadingTrumps: Set[Card] => Option[Int] = (cards: Set[Card]) =>
        Some(countUnter(cards) + countColorTrumps(cards, trump))
}

case class Grand(hand: Boolean, schneider: Boolean, angesagt: Boolean, schwarz: Boolean, ouvert: Boolean) extends Game {

    override implicit def orderOfValues: Ordering[Value] = normalOrdering

    override def isTrump: Card => Boolean = (card: Card) => card.value == Unter

    override def totalPoints: Set[Card] => Int = ???

    override def leadingTrumps: Set[Card] => Option[Int] = (cards: Set[Card]) => Some(countUnter(cards))
}
