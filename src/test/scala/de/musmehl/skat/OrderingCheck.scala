package de.musmehl.skat

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import de.musmehl.skat.Generators

class OrderingCheck extends Properties("Ordering"){

    property("of Colors") = forAll(Generators.genPermutationOfColors) { input =>
        import de.musmehl.skat.colorOrdering

        input.sorted == List[Color](Schell, Rot, Grün, Eichel)
    }

}
