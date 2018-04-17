package de.musmehl.skat

sealed trait Game {

    implicit def orderOfValues: Ordering[Value]

    def isTrump(card: Card): Boolean

    def schneider: Boolean

    def schneiderAngesagt: Boolean

    def schwarz: Boolean

    def schwarzAngesagt: Boolean

    def ouvert: Boolean

    def hand: Boolean

    def totalGamePoints(cardsSinglePlayer: Set[Card]): Int

    def leadingTrumps(cards: Set[Card]): Int

}

/**
  * Rules and methods for Null game
  */
case class Null(hand: Boolean, ouvert: Boolean) extends Game {

    override implicit def orderOfValues: Ordering[Value] = nullOrdering

    override def schneider: Boolean = false

    override def schneiderAngesagt: Boolean = false

    override def schwarz: Boolean = false

    override def schwarzAngesagt: Boolean = false

    override def totalGamePoints(cardsSinglePlayer: Set[Card]): Int = {
        (hand, ouvert) match {
            case (true, false) => 35
            case (true, true) => 56
            case (false, true) => 45
            case (false, false) => 23
        }
    }

    override def isTrump(card: Card): Boolean = false

    override def leadingTrumps(cards: Set[Card]): Int = 0
}

case class ColorGame(trump: Color,
                     hand: Boolean,
                     schneider: Boolean,
                     schneiderAngesagt: Boolean,
                     schwarz: Boolean,
                     schwarzAngesagt: Boolean,
                     ouvert: Boolean) extends Game {

    // enforce Skat rules
    require( !schneiderAngesagt || hand, "Schneider can only be called if playing Hand")
    require( !schwarzAngesagt || hand, "Schwarz can only be called if playing Hand")
    require( !ouvert || hand, "Ouvert can only be called if playing Hand")

    override implicit def orderOfValues: Ordering[Value] = normalOrdering

    override def isTrump(card: Card): Boolean = card.color == trump || card.value == Unter

    override def totalGamePoints(cardsSinglePlayer: Set[Card]): Int =
        (leadingTrumps(cardsSinglePlayer) + 1 +
            modifications(hand, schneider, schneiderAngesagt, schwarz, schwarzAngesagt, ouvert)) * trump.colorValue

    override def leadingTrumps(cards: Set[Card]): Int = {
        val (unter, mit) = countUnter(cards)
        unter + countColorTrumps(cards, trump, mit)
    }
}

case class Grand(hand: Boolean,
                 schneider: Boolean,
                 schneiderAngesagt: Boolean,
                 schwarz: Boolean,
                 schwarzAngesagt: Boolean,
                 ouvert: Boolean) extends Game {

    // enforce Skat rules
    require( !schneiderAngesagt || hand, "Schneider can only be called if playing Hand")
    require( !schwarzAngesagt || hand, "Schwarz can only be called if playing Hand")
    require( !ouvert || (hand && schneider && schneiderAngesagt && schwarz && schwarzAngesagt),
        "Ouvert can only be called if playing Hand")

    override implicit def orderOfValues: Ordering[Value] = normalOrdering

    override def isTrump(card: Card): Boolean = card.value == Unter

    override def totalGamePoints(cardsSinglePlayer: Set[Card]): Int = {
        val gameValue = if (ouvert) 36 else 24

        (leadingTrumps(cardsSinglePlayer) + 1 +
            modifications(hand, schneider, schneiderAngesagt, schwarz, schwarzAngesagt, ouvert)) * gameValue
    }

    override def leadingTrumps(cards: Set[Card]): Int = countUnter(cards)._1
}
