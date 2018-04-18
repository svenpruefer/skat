package de.musmehl.skat

import org.scalacheck.Gen

object Generators {

    val genColor: Gen[Color] = Gen.oneOf(Schell, Rot, Grün, Eichel)

    val genPermutationOfColors: Gen[List[Color]] = Gen.pick(4, List[Color](Schell, Rot, Grün, Eichel)).map(_.toList)

}
