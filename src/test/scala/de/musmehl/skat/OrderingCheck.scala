package de.musmehl.skat

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

class OrderingCheck extends Properties("Ordering") {

  property("of Colors") = forAll(Generators.genPermutationOfColors) { input =>
    input.sorted(colorOrdering) == List[Color](Schell, Rot, Grün, Eichel)
  }

  property("of Values in Null") = forAll(Generators.genPermutationOfNullValues) { input =>
    input.sorted(nullOrdering) == List[Value](Sieben, Acht, Neun, Zehn, Unter, Ober, König, Ass)
  }

  property("of Values in a Colorgame and Grand") = forAll(Generators.genPermutationOfColorGrandValues) { input =>
    input.sorted(normalOrdering) == List[Value](Sieben, Acht, Neun, Ober, König, Zehn, Ass)
  }
}
