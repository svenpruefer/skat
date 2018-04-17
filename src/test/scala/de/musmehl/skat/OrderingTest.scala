package de.musmehl.skat

import org.scalatest.FunSuite

class OrderingTest extends FunSuite {

    test("Simple Ordering Test of Colors") {
        import de.musmehl.skat.colorOrdering

        val input = List[Color](Grün, Eichel, Rot, Schell)

        assert(input.sorted == List[Color](Schell, Rot, Grün, Eichel))
    }

}
