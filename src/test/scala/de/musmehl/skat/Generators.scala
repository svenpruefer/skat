package de.musmehl.skat

import org.scalacheck.Gen

object Generators {

    val genColor: Gen[Color] = Gen.oneOf(Schell, Rot, Grün, Eichel)

    val genValue: Gen[Value] = Gen.oneOf(Sieben, Acht, Neun, Zehn, Unter, Ober, König, Ass)

    val genPermutationOfColors: Gen[List[Color]] = Gen.pick(4, List[Color](Schell, Rot, Grün, Eichel)).map(_.toList)

    val genPermutationOfNullValues: Gen[List[Value]] =
        Gen.pick(8, List[Value](Sieben, Acht, Neun, Zehn, Unter, Ober, König, Ass)).map(_.toList)

    val genPermutationOfColorGrandValues: Gen[List[Value]] =
        Gen.pick(7, List[Value](Sieben, Acht, Neun, Zehn, Ober, König, Ass)).map(_.toList)

    val genPlayer: Gen[Player] = for {
        name <- Gen.alphaStr
        points <- Gen.posNum[Int]
    } yield Player(name, points)

    val genThreePlayers: Gen[List[Player]] = Gen.listOfN(3, genPlayer) suchThat (x => x.distinct == x)
}
