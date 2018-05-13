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

import org.scalacheck.{ Gen, Properties }
import org.scalacheck.Prop.forAll

import scala.collection.immutable.Queue

object QueueCheck extends Properties("Queuing of players") {

    property("must be correct during a Stich") = forAll(Generators.genThreePlayers, Gen.oneOf(0, 1, 2)) {
        (threePlayers, newLead) =>
            val originalQueue = Queue(threePlayers: _*)

            val newQueue = adaptOrderOfPlayers(originalQueue, threePlayers(newLead))

            val (firstNewPlayer, remainingPlayers) = newQueue.dequeue

            val secondPlayer = remainingPlayers.dequeue._1

            firstNewPlayer == threePlayers(newLead) && secondPlayer == threePlayers((newLead + 1) % 3)
    }
}
