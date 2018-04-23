package de.musmehl.skat

trait MatchBase {

    def id: String

    def players: List[Player]

    def dealCards: MatchState

    def reizen(matchState: MatchState): MatchState

    def playRound(matchState: MatchState): MatchState

    def playFullGame: Map[Player, Int]

}

case class Match(id: String, players: List[Player]) extends MatchBase {

    require(players.length == 3, "Skat can only be played by exactly three players")

    override def dealCards: MatchState = {
        val shuffledCards = scala.util.Random.shuffle(Card.allCards)

        val noDealtCards = Map[Player, Set[Card]](
            (players.head, Set.empty[Card]),
            (players(1), Set.empty[Card]),
            (players(2), Set.empty[Card])
        )

        val (dealtCards, skat) = shuffledCards.foldLeft((noDealtCards, Set.empty[Card])) ((agg, el) => {
            shuffledCards.indexOf(el) match {
                case i: Int if Set(0, 1, 2, 11, 12, 13, 14, 23, 24, 25).contains(i) =>
                    (agg._1.updated(players.head, agg._1(players.head) + el), agg._2)
                case i: Int if Set(3, 4, 5, 15, 16, 17, 18, 26, 27, 28).contains(i) =>
                    (agg._1.updated(players(1), agg._1(players(1)) + el), agg._2)
                case i: Int if Set(6, 7, 8, 19, 20, 21, 22, 29, 30, 31).contains(i) =>
                    (agg._1.updated(players(2), agg._1(players(2)) + el), agg._2)
                case i: Int if Set(9, 10).contains(i) => (agg._1, agg._2 + el)
            }
        })

        MatchState(
            Map[Player, PlayerState](
                (players.head, PlayerState(dealtCards(players.head), Set.empty[Stich], weg = false)),
                (players(1), PlayerState(dealtCards(players(1)), Set.empty[Stich], weg = false)),
                (players(2), PlayerState(dealtCards(players(2)), Set.empty[Stich], weg = false))
            ),
            players,
            skat,
            None,
            None
        )
    }

    override def reizen(matchState: MatchState): MatchState = ???

    override def playRound(matchState: MatchState): MatchState = ???

    override def playFullGame: Map[Player, Int] = ???
}

case class MatchState(playerStates: Map[Player, PlayerState],
                      order: List[Player],
                      skat: Set[Card],
                      game: Option[Game],
                      singlePlayer: Option[Player])
