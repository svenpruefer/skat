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
