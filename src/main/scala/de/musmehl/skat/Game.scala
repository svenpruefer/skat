package de.musmehl.skat

sealed trait Game {

    implicit val orderOfValues: Ordering[Value]

    def isTrump(card: Card): Boolean

    def schneiderAngesagt: Boolean

    def schwarzAngesagt: Boolean

    def ouvert: Boolean

    def hand: Boolean

    def gameWorth(cardsSinglePlayer: Set[Card], schneider: Boolean, schwarz: Boolean): Int

    def leadingTrumps(cards: Set[Card]): Int

    def totalPointsForSinglePlayer(cardsSinglePlayer: Set[Card], sticheSinglePlayer: Set[Stich]): Int

    def determineWinnerOfStich(leadPlayer: PlayerBase, stich: Map[PlayerBase, Card]): PlayerBase = {

        require(stich.keySet.contains(leadPlayer), "First player during stich needs to be contained in the set of players")

        val playedColor = stich(leadPlayer).color

        val winnerCard = if (!stich.values.exists(x => isTrump(x))) {
            findHighestCardAmongNonTrumps(playedColor, stich.values)
        } else {
            findHighestCardAmongTrumps(stich.values.filter(x => isTrump(x)))
        }

        stich.keys.find(x => stich(x) == winnerCard).get
    }

    private def findHighestCardAmongNonTrumps(playedColor: Color, cards: Iterable[Card]): Card = {
        require(cards.forall(x => !isTrump(x)), "All cards need to be non-trumps")

        cards.filter(x => x.color == playedColor).maxBy(x => x.value)
    }

    private def findHighestCardAmongTrumps(cards: Iterable[Card]): Card = {
        require(cards.forall(x => isTrump(x)), "All cards need to be trumps")

        if (cards.exists(x => x.value == Unter)) {
            cards.filter(x => x.value == Unter).maxBy(x => x.color)(colorOrdering)
        } else {
            cards.maxBy(x => x.value)
        }
    }

    def moveIsValid(cardsInHand: Set[Card], firstCard: Card): Card => Boolean = (x: Card) => {
        if (isTrump(firstCard)) {
            val hasTrump = cardsInHand.exists(p => isTrump(p))
            if (hasTrump) isTrump(x) else true
        } else {
            val hasColor = cardsInHand.exists(p => p.color == firstCard.color && p.value != Unter)
            if (hasColor) x.color == firstCard.color && x.value != Unter else true
        }
    }

}

/**
 * Rules and methods for Null game
 */
case class Null(hand: Boolean, ouvert: Boolean) extends Game {

    override implicit val orderOfValues: Ordering[Value] = nullOrdering

    override def schneiderAngesagt: Boolean = false

    override def schwarzAngesagt: Boolean = false

    override def gameWorth(cardsSinglePlayer: Set[Card], schneider: Boolean, schwarz: Boolean): Int = {
        (hand, ouvert) match {
            case (true, false)  => 35
            case (true, true)   => 56
            case (false, true)  => 45
            case (false, false) => 23
        }
    }

    override def isTrump(card: Card): Boolean = false

    override def leadingTrumps(cards: Set[Card]): Int = 0

    override def totalPointsForSinglePlayer(cardsSinglePlayer: Set[Card], sticheSinglePlayer: Set[Stich]): Int = {
        val points = gameWorth(cardsSinglePlayer, schneider = false, schwarz = false)
        if (sticheSinglePlayer.isEmpty) points
        else {
            if (hand) {
                -points
            } else {
                -2 * points
            }
            // TODO check rules if loosing a Null Hand does not double the minus points
        }
    }

    override def moveIsValid(cardsInHand: Set[Card], firstCard: Card): Card => Boolean = (x: Card) => {
        firstCard.color == x.color || !cardsInHand.exists(p => p.color == firstCard.color)
    }
}

case class ColorGame(
    trump:             Color,
    hand:              Boolean,
    schneiderAngesagt: Boolean,
    schwarzAngesagt:   Boolean,
    ouvert:            Boolean
) extends Game {

    // enforce Skat rules
    require(!schneiderAngesagt || hand, "Schneider can only be called if playing Hand")
    require(!schwarzAngesagt || hand, "Schwarz can only be called if playing Hand")
    require(!ouvert || hand, "Ouvert can only be called if playing Hand")

    override implicit val orderOfValues: Ordering[Value] = normalOrdering

    override def isTrump(card: Card): Boolean = card.color == trump || card.value == Unter

    override def gameWorth(cardsSinglePlayer: Set[Card], schneider: Boolean, schwarz: Boolean): Int =
        (leadingTrumps(cardsSinglePlayer) + 1 +
            modifications(hand, schneider, schneiderAngesagt, schwarz, schwarzAngesagt, ouvert)) * trump.colorValue

    override def leadingTrumps(cards: Set[Card]): Int = {
        val (unter, mit) = countUnter(cards)
        unter + countColorTrumps(cards, trump, mit)
    }

    override def totalPointsForSinglePlayer(cardsSinglePlayer: Set[Card], sticheSinglePlayer: Set[Stich]): Int = {
        val pointsSinglePlayer = sticheSinglePlayer.map(_.totalValue).sum
        val schwarz = sticheSinglePlayer.size == 10
        val schneider = pointsSinglePlayer > 90
        // TODO Confirm that loosing with less than 30 points does not count as Schneider

        val win = (!schneiderAngesagt || schneider) && (!schwarzAngesagt || schwarz) && pointsSinglePlayer > 60
        val points = gameWorth(cardsSinglePlayer, schneider, schwarz)
        if (win) points else -2 * points
    }
}

case class Grand(
    hand:              Boolean,
    schneiderAngesagt: Boolean,
    schwarzAngesagt:   Boolean,
    ouvert:            Boolean
) extends Game {

    // enforce Skat rules
    require(!schneiderAngesagt || hand, "Schneider can only be called if playing Hand")
    require(!schwarzAngesagt || hand, "Schwarz can only be called if playing Hand")
    require(
        !ouvert || (hand && schneiderAngesagt && schwarzAngesagt),
        "Ouvert can only be called if playing Hand"
    )

    override implicit val orderOfValues: Ordering[Value] = normalOrdering

    override def isTrump(card: Card): Boolean = card.value == Unter

    override def gameWorth(cardsSinglePlayer: Set[Card], schneider: Boolean, schwarz: Boolean): Int = {
        val gameValue = if (ouvert) 36 else 24

        (leadingTrumps(cardsSinglePlayer) + 1 +
            modifications(hand, schneider, schneiderAngesagt, schwarz, schwarzAngesagt, ouvert)) * gameValue
    }

    override def leadingTrumps(cards: Set[Card]): Int = countUnter(cards)._1

    override def totalPointsForSinglePlayer(cardsSinglePlayer: Set[Card], sticheSinglePlayer: Set[Stich]): Int = {
        val pointsSinglePlayer = sticheSinglePlayer.map(_.totalValue).sum
        val schwarz = sticheSinglePlayer.size == 10
        val schneider = pointsSinglePlayer > 90

        val win = (!schneiderAngesagt || schneider) && (!schwarzAngesagt || schwarz) && pointsSinglePlayer > 60
        val points = gameWorth(cardsSinglePlayer, schneider, schwarz)
        if (win) points else -2 * points
    }
}
