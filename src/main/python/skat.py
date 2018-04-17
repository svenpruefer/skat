####################################
# Import Libraries for later usage #
####################################

import random

#######################
# Definition of cards #
#######################

class card:
    # This class implements porperties of cards. In particular a card knows its color and face, its value and wheter or not it is a trump at given moment in time.
    
    def __init__(self,color,face):
        # Cards  have the following attributes:
        # Color (String): s,r,g,e
        # Face (String): 7,8,9,10,u,o,k,a
        # Value (Int): 0,2,3,4,10,11
        # Trump (Boolean): True or False
        
        self.color = color
        self.face = face
        self.value = 0
        if (face == "7") or (face == "8") or (face == "9"):
            self.value = 0
        elif face == "10":
            self.value = 10
        elif face == "u":
            self.value = 2
        elif face == "o":
            self.value = 3
        elif face == "k":
            self.value = 4
        elif face == "a":
            self.value = 11
        self.trump = False
        self.name = color + str(face)
    
    def trump(self):
        self.trump = True

# Initialize cards

s7 = card("s","7")
s8 = card("s","8")
s9 = card("s","9")
su = card("s","u")
so = card("s","o")
sk = card("s","k")
s10 = card("s","10")
sa = card("s","a")

r7 = card("r","7")
r8 = card("r","8")
r9 = card("r","9")
ru = card("r","u")
ro = card("r","o")
rk = card("r","k")
r10 = card("r","10")
ra = card("r","a")

g7 = card("g","7")
g8 = card("g","8")
g9 = card("g","9")
gu = card("g","u")
go = card("g","o")
gk = card("g","k")
g10 = card("g","10")
ga = card("g","a")

e7 = card("e","7")
e8 = card("e","8")
e9 = card("e","9")
eu = card("e","u")
eo = card("e","o")
ek = card("e","k")
e10 = card("e","10")
ea = card("e","a")

all_cards = [s7,s8,s9,su,so,sk,s10,sa,r7,r8,r9,ru,ro,rk,r10,ra,g7,g8,g9,gu,go,gk,g10,ga,e7,e8,e9,eu,eo,ek,e10,ea]

# Initialize Skat

class skat_karten:
    def __init__(self):
        self.contents = []
        self.owner = None

skat = skat_karten()

##############################
# Helper functions for games #
##############################

def vergleich(card1,card2, order_of_cards):
    winner = card1
    order_of_unter = ["s","r","g","e"]
    
    # If Player 1 does not play any trump
    if card1.trump == False:
        #print str(card2.trump) + " " + str(card2.face)  
        if card2.trump == True:
            winner = card2
        elif card2.color == card1.color and order_of_cards.index(card2.face) > order_of_cards.index(card1.face):
            winner = card2
        
    # If Player 1 plays a trump
    if (card1.trump == True) and (card2.trump == True):
        if card1.face != "u" and card2.face == "u":
            winner = card2
        elif ( card1.face == "u" and card2.face == "u" ) and order_of_unter.index(card2.color) > order_of_unter.index(card1.color):
            winner = card2
        elif card1.face != "u" and card2.face != "u" and order_of_cards.index(card2.face) > order_of_cards.index(card1.face):
            winner = card2
    
    return winner

def stich(cards,order_of_cards):
    winner = vergleich(cards[0],cards[1],order_of_cards)
    winner = vergleich(winner,cards[2],order_of_cards)
    return winner
    

##################
# Define Players #
##################

class spieler:
    # Class stores cards on hand, provides a function to compute possible (lawful) moves, keeps track of won cards, provides function for decision maker (at the moment random)
    
    def __init__(self,cards,name):
        self.cards = cards
        self.stiche = []
        self.name = name
        self.points = 0
        self.total_points = 0
        
    # Incorporates Rules for playing cards, i.e. if you can follow suite you have to, otherwise you are free to play anything
    def possible_moves(self,played_card = None):
        moves = []
        if played_card is None:
            return self.cards
        else:
            if played_card.trump == False:
                for card in self.cards:
                    if card.color == played_card.color and card.trump == False:
                        moves.append(card)
                if len(moves) == 0:
                    moves = self.cards
            else:
                for card in self.cards:
                    if card.trump == True:
                        moves.append(card)
                if len(moves) == 0:
                    moves = self.cards
        return moves
    
    # Play a card, parameters should be added here according to how the AI should make its decisions
    def play(self,played_cards = None):
        if played_cards is None:
            random_card = random.choice(self.possible_moves())
        else:
            random_card = random.choice(self.possible_moves(played_cards[0])) # Make random lawful choice
        del self.cards[self.cards.index(random_card)]
        return random_card
           
    # Hand a card
    def hand(self,card):
        self.cards.append(card)
    
    # Win a set of three cards
    def win(self,cards):
        self.stiche = self.stiche + cards
    
    # Calculate points
    def points(self):
        summe = 0
        for card in self.stiche:
            summe = summe + card.value
        return summe

    # Exchange cards with skat
    def exchange_cards_with_skat(self):
        for i in [0,1]:
            random_card = random.choice(self.cards)
            self.cards[self.cards.index(random_card)] = skat.contents[i]
            skat.contents[i] = random_card

    # Bid for Reizen
    def bid(self, wert):
        # This method decides which value a player bids when he is asked to raise during Reizen. At the moment this is just purely random.
        
        possible_reiz_values = [18,20,22,23,24,27,30,35,36,40,42,44,45,46,48,50,56,54,60,66,70,72]
        choice = random.choice(possible_reiz_values)
        if wert == None or choice > wert:
            return "raise", choice
        else:
            return "weg", None

    # React to a bid during Reizen
    def react_to_bid(self,wert):
        # This method decides how a player reacts when he is aked to react to a bid during Reizen. At the moment this is just purely random.
        
        possible_reiz_values = [18,20,22,23,24,27,30,35,36,40,42,44,45,46,48,50,56,54,60,66,70,72]
        choice = random.choice(possible_reiz_values)
        if choice > wert:
            return "raise", choice
        else:
            return "weg", None        

    # Decide for a game after winning Reizen
    def decide_game(self,wert):
        # This method decides on a game if the player wins Reizen and is thus single player. At the moment it just plays a random colored game.
        
        possible_farbe = ["gruen", "shell", "rot", "eichel"]
        trumpf_farbe = random.choice(possible_farbe)

        if trumpf_farbe == "gruen":
            truempfe = [g7,g8,g9,go,gk,g10,ga]
            truempfe.reverse()
            list_of_trumps = [eu,gu,ru,su]+truempfe
        elif trumpf_farbe == "rot":
            truempfe = [r7,r8,r9,ro,rk,r10,ra]
            truempfe.reverse()
            list_of_trumps = [eu,gu,ru,su]+truempfe
        elif trumpf_farbe == "shell":
            truempfe = [s7,s8,s9,so,sk,s10,sa]
            truempfe.reverse()
            list_of_trumps = [eu,gu,ru,su]+truempfe
        elif trumpf_farbe == "eichel":
            truempfe = [e7,e8,e9,eo,ek,e10,ea]
            truempfe.reverse()
            list_of_trumps = [eu,gu,ru,su]+truempfe

        if eu in self.cards:
            position = 0
            while position <= 9 and list_of_trumps[position +1] in self.cards: # Also check Skat, not just hand cards.
                position = position + 1

            unter_wert = position + 1
        else:
            position = 0
            while position <= 9 and not list_of_trumps[position +1] in self.cards: # Also check Skat, not just hand cards.
                position = position + 1

            unter_wert = position + 1
        
        game = spiel(typ="farbe",unter=unter_wert,reizwert=wert,trumpf=trumpf_farbe)
        
        return game
        
#################################################
# Define possible games and corresponding class #
#################################################

class spiel:
    # This class saves the settings and rules for the game. Also it can apply the rules to all cards contained in a list of cards.

    def __init__(self,typ, unter = None, reizwert = 0, hand = False, schneider = False, schneider_angesagt = False, schwarz = False, schwarz_angesagt = False, ouvert = False, trumpf = None):
        # Initialize data
        # Possible typ (String): farbe, null, grand
        # Possible unter (Int): 1-12
        # Possible reizwert (Int): 17-360 (?)
        # Possible hand, schneider, schwarz, ouvert (Boolean): true, false
        # Possible trumpf (String): rot, gruen, eichel, shell; gets ignored if type != farbe
        
        self.typ = typ
        self.schneider = schneider
        self.schneider_angesagt = schneider_angesagt
        self.schwarz = schwarz
        self.schwarz_angesagt = schwarz_angesagt
        self.ouvert = ouvert
        self.trumpf = trumpf
        self.unter = unter
        self.hand = hand
        self.reizwert = reizwert

        # Determine order of cards
        
        if self.typ == "null":
            order_of_cards = ["7","8","9","10","u","o","k","a"]
        else:
            order_of_cards = ["7","8","9","o","k","10","a"]
        
        self.order_of_cards = order_of_cards

        # Determine worth of game

        if self.typ == "farbe":
            faktor = unter + 1 + int(schwarz) + int(schneider) + int(ouvert) + int(schwarz_angesagt) + int(schneider_angesagt)
            if self.trumpf == "shell":
                self.worth = faktor * 9
            elif self.trumpf == "rot":
                self.worth = faktor * 10
            elif self.trumpf == "gruen":
                self.worth = faktor * 11
            elif self.trumpf == "eichel":
                self.worth = faktor * 12
        elif self.typ == "grand":
            if self.ouvert == False:
                faktor = unter + 1 + int(schwarz) + int(schneider) + int(schwarz_angesagt) + int(schneider_angesagt)
                self.worth = faktor * 24
            else:
                faktor = unter + 1 # Fixme: This is wrong, rules?
                self.worth = faktor * 36
        else:
            if ouvert == True and hand == True:
                self.worth = 56
            elif ouvert == True and hand == False:
                self.worth = 46 # Fixme: Is this correct?
            elif ouvert == False and hand == True:
                self.worth = 35
            else:
                self.worth = 23
        
    def apply_to_cards(self):
        # Applies trump value to all cards according to game
        
        if self.typ  == "farbe":
            if self.trumpf == "shell":
                for card in all_cards:
                    if card.color == "s" or card.face == "u":
                        card.trump = True
                    else:
                        card.trump = False
            elif self.trumpf  == "rot":
                for card in all_cards:
                    if card.color == "r" or card.face == "u":
                        card.trump = True
                    else:
                        card.trump = False
            elif self.trumpf  == "gruen":
                for card in all_cards:
                    if card.color == "g" or card.face =="u":
                        card.trump = True
                    else:
                        card.trump = False
            elif self.trumpf  == "eichel":
                for card in all_cards:
                    if card.color == "e" or card.face == "u":
                        card.trump = True
                    else:
                        card.trump = False
        elif self.typ == "grand":
            for card in all_cards:
                if card.face == "u":
                    card.trump = True
                else:
                    card.trump = False
                    
##################    
# Start new game #
##################

def play_game(game,players,single_player):
    # Player 1 starts, First verify, that single player is indeed part of the game
    if not single_player in players:
        print("Single player not contained in list of players")
  
    # Define trumps and card orders for possible games

    game.apply_to_cards()
    
    # Go through all rounds and play the game 
    
    dran = 0
    for i in range(10):
        cards_in_stich = [None,None,None]
        cards_in_stich[dran] = players[dran].play()
        #print(cards_in_stich)
        #print(dran)
        #print(players)
        cards_in_stich[(dran + 1)%3] = players[(dran + 1)%3].play([cards_in_stich[dran]])
        cards_in_stich[(dran + 2)%3] = players[(dran + 2)%3].play([cards_in_stich[dran],cards_in_stich[(dran + 1)%3]])
        winner = players[cards_in_stich.index(stich(cards_in_stich,game.order_of_cards))]
        winner.win(cards_in_stich)
        dran = players.index(winner)

    # Add Skat to Stiche of single player

    single_player.win(skat.contents)
    
    # Determine winning conditions and points
    
    punkte = [player.points for player in players]
        
    return punkte

####################
# Distribute cards #
####################

def distribute_cards(players):
    # This function deals cards among the three players in the argument in the Skat standard order. It acts on the players via their hand-methods. The type of "skat" is still to be determined.
    
    random.shuffle(all_cards)
    for i in range(len(all_cards)):
        # Distribtion scheme
        if i in [0,1,2,11,12,13,14,23,24,25]:
            players[0].hand(all_cards[i])
        if i in [3,4,5,15,16,17,18,26,27,28]:
            players[1].hand(all_cards[i])
        if i in [6,7,8,19,20,21,22,29,30,31]:
            players[2].hand(all_cards[i])
        if i in [9,10]:
            skat.contents.append(all_cards[i])
    

##########################################################
# Play one game for a fixed ordered set of three players #
##########################################################

def initialize_a_game(players):
    # This function deals cards, executes Reizen, calculates necessary conditions for winning, executes the game and returns results.
    # Input: List(Player), it takes as arguments the three players in the standard order of Skat, i.e. the first player deals the cards and the second player starts playing.
    # Output: Dict(Player:Points)
    
    # Distribute cards in the "correct" way
    distribute_cards(players)

    # Execute Reizen
    game, single_player = reizen(players)

    if game.typ != "eingeben":
        # Single player can exchange cards with Skat, if he didn't play "hand"
        if game.hand == False:
            single_player.exchange_cards_with_skat()
            
        # Give Skat to single player
        skat.owner = single_player
        
        # Play the game!
        
        punkte = play_game(game, players, single_player)
        
        # Determine if winning conditions have been met and then distribute points
        conditions = True
        team_points = 0
        for player in players:
            if player != single_player:
                team_points = team_points + player.points
                
        if game.schwarz_angesagt == True:
            if len(single_player.stiche) < 32:
                conditions = False
        if  len(single_player.stiche) == 32:
            game.schwarz = True
        if game.schneider_angesagt == True:
            if team_points > 30:
                conditions = False
        if team_points <= 30:
            game.schneider = True
            
        results = {}
        if game.typ == "null":
            if len(single_player.stiche) == 2 and conditions == True:
                for player in players:
                    if player == single_player:
                        results[player] = game.worth
                    else:
                        results[player] = 0
            else:
                for player in players:
                    if player == single_player:
                        if game.hand == False:
                            results[player] = - 2 * game.worth
                        else:
                            results[player] = - game.worth
                    else:
                        results[player] = 30 # Does this depend on the number of players?
        else:
            if single_player.points > 60 and conditions == True:
                for player in players:
                    if player == single_player:
                        results[player] = game.worth
                    else:
                        results[player] = 0
            else:
                for player in players:
                    if player == single_player:
                        if game.hand == False:
                            results[player] = - 2 * game.worth
                        else:
                            results[player] = - game.worth
                    else:
                        results[player] = 30 # Does this depend on the number of players?
                        
        # Collect cards and reset game
        for player in players:
            player.cards = []
            player.points = 0
            player.stiche = []
        skat.contents = []
        skat.owner = None
        #game.kill()
        
        return results
    else:
        results = {0 for player in players}
        return results
    
#################
# Play a series #
#################

def play_a_series(names_of_players, number_of_rounds):
    # This function plays a number_of_rounds of rounds of N games for N players with given names and gives back their results. Of course N=3,4 only.
    
    # Create players and count if there are three or four
    list_of_players = [spieler([],name) for name in names_of_players]
    N = len(list_of_players)
    skat = skat_karten()
    
    # Play games and add points to respective player
    for round in range(number_of_rounds):
        if N == 3:
            for geber in range(N):
                order_of_players = [list_of_players[geber],list_of_players[(geber + 1) % 3],list_of_players[(geber + 2) % 3]]
                
                results = initialize_a_game(order_of_players)
                print(results)
                for player in order_of_players:
                    player.total_points = player.total_points + results[player]
        if N == 4:
            for geber in range(N):
                order_of_players = [list_of_players[(geber + 1) % 4],list_of_players[(geber + 2) % 4],list_of_players[(geber + 3) % 4]]
                results = initialize_a_game(order_of_players)
                for player in order_of_players:
                    player.total_points = player.total_points + results[player]

    # Print results
    prepared_string = ""
    for player in list_of_players:
        prepared_string = prepared_string + "The player " + player.name + " has " + str(player.total_points) + " Points. "
    print(prepared_string)

##########
# Reizen #
##########

def reizen(players):
    # This function implements the Reizen procedure. It calls the players in the correct Skat order (assuming the first player has dealt the cards) to ask to raise their bid and then decides who wins the game. Next that player needs to decide what to actually play. This decision is saved as the game and returned together with the actual single player.

    wert = None
    queue = list(players)
    single_player = None
    
    while len(queue) > 1:
        if len(queue) == 3:
            descision_2, new_wert = players[2].bid(wert)
            if descision_2 == "weg":
                del queue[2]
            else:
                wert = new_wert
                descision_1 = players[1].react_to_bid(new_wert)
                if descision_1 == "weg":
                    del queue[1]
        else:
            descision_0, new_wert = players[0].bid(wert)
            if descision_0 == "weg":
                del queue[0]

            else:
                wert = new_wert
                descision = queue[1].react_to_bid(new_wert)
                if descision == "weg":
                    del queue[1]

    if wert < 18 or wert == None:
        game = spiel("eingeben")
        single_player = None
    else:
        game = queue[0].decide_game(wert) 
        single_player = queue[0]
        
    # Return results
    return game, single_player

#player = spieler([],"A")
#print(player.cards)
#print(type(player))
#skat = skat_karten()
#skat.contents
#print(skat.contents)
play_a_series(["A","B","C"],1)
