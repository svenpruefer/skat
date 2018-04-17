package de.musmehl

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

    implicit val nullOrdering: Ordering[Value] = Ordering.by(nullMap)

    private val normalMap: Map[Value, Int] = Map(
        (Sieben, 1),
        (Acht, 2),
        (Neun, 3),
        (Ober, 4),
        (König, 5),
        (Zehn, 6),
        (Ass, 7)
    )

    implicit val normalOrdering: Ordering[Value] = Ordering.by(normalMap)

    private val colorMap: Map[Color, Int] = Map(
        (Schell, 1),
        (Rot, 2),
        (Grün, 3),
        (Eichel, 4)
    )

    implicit val colorOrdering: Ordering[Color] = Ordering.by(colorMap)

    // endregion

    // region Determination of suits of trumps

    def countUnter(cards: Set[Card]): Int = ???

    def countColorTrumps(cards: Set[Card], trumpColor: Color): Int = ???

    // endregion
}
