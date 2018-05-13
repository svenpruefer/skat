// Copyright 2018 Sven Prüfer
//
// This file is part of skat.
//
// skat is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// skat is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with skat.  If not, see <http://www.gnu.org/licenses/>.

package de.musmehl.skat

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object OrderingCheck extends Properties("Ordering") {

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
