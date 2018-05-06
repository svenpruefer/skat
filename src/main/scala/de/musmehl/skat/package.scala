package de.musmehl

import scala.collection.immutable.Queue

package object skat {

    // region Orderings of values and colors

    private val nullMap: Map[Value, Int] = Map(
        (Sieben, 1),
        (Acht, 2),
        (Neun, 3),
        (Zehn, 4),
        (Unter, 5),
        (Ober, 6),
        (König, 7),
        (Ass, 8)
    )

    val nullOrdering: Ordering[Value] = Ordering.by(nullMap)

    private val normalMap: Map[Value, Int] = Map(
        (Sieben, 1),
        (Acht, 2),
        (Neun, 3),
        (Ober, 4),
        (König, 5),
        (Zehn, 6),
        (Ass, 7)
    )

    val normalOrdering: Ordering[Value] = Ordering.by(normalMap)

    private val colorMap: Map[Color, Int] = Map(
        (Schell, 1),
        (Rot, 2),
        (Grün, 3),
        (Eichel, 4)
    )

    implicit val colorOrdering: Ordering[Color] = Ordering.by(colorMap)

    // endregion

    // region Determination of suits of trumps

    def countUnter(cards: Set[Card]): (Int, Boolean) = {
        val existingUnter: Set[Int] = cards.filter(x => x.value == Unter).map(_.color).map(colorMap)

        val mit = existingUnter.contains(4)

        List(4, 3, 2, 1).indexWhere(x => existingUnter.contains(x) != mit) match {
            case -1 => (4, mit)
            case x  => (x, mit)
        }
    }

    def countColorTrumps(cards: Set[Card], trumpColor: Color, mit: Boolean): Int = {
        val existingColorTrumps: Set[Int] = cards.filter(x => x.color == trumpColor && x.value != Unter)
            .map(_.value).map(normalMap)

        (7 until 1).toList.indexWhere(x => existingColorTrumps.contains(x) != mit) match {
            case -1 => 7
            case x  => x
        }
    }

    // endregion

    def modifications(
        hand:              Boolean,
        schneider:         Boolean,
        schneiderAngesagt: Boolean,
        schwarz:           Boolean,
        schwarzAngesagt:   Boolean,
        ouvert:            Boolean
    ): Int = {
        List(hand, schneider, schneiderAngesagt, schwarz, schwarzAngesagt, ouvert).count(identity)
    }

    def adaptOrderOfPlayers(oldOrder: Queue[Player], newLead: Player): Queue[Player] = {
        require(oldOrder.size == 3, "Adapting the order of players in Skat only possbile for three players")
        require(oldOrder.contains(newLead), "In order to adapt the order of players the new lead needs to be part of the old queue")

        val positionNewLead = oldOrder.indexOf(newLead)
        val (first, second) = oldOrder.splitAt(positionNewLead)
        second.enqueue(first)
    }

}
