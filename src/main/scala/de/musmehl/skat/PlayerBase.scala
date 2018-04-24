package de.musmehl.skat

trait PlayerBase {

    def name: String

    def totalPoints: Int

    def sagt(reizValue: Option[Int]): Option[Int]

    def hört(reizValue: Int): Boolean

    def plays(playerState: PlayerState, game: Game, onTable: Set[Card]): Card
}

case class Player(name: String,
                  totalPoints: Int) extends PlayerBase

case class PlayerState(cards: Set[Card], stiche: Set[Stich], weg: Boolean) {

    def playCard(card: Card): PlayerState = {
        require(cards.contains(card), "In order to play a card, this card must be in possession of the player")
        copy(cards = this.cards - card)
    }
}
