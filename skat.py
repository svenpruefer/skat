# Import Libraries for later usage
import random

# Define cards
class card:
    # Cards  have the following attributes: Color (s,r,g,e), Face (7,8,9,10,u,o,k,a), Value (for later evaluation, possibly 0,2,3,4,10,11), Trump (True or False)
    def __init__(self,color,face):
        self.color = color
        self.face = face
        self.value = 0
        if (face == 7) or (face == 8) or (face == 9):
            self.value = 0
        elif face == 10:
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

# Function to decide who wins a round

def vergleich(card1,card2, order_of_cards):
    winner = card1
    order_of_unter = ["s","r","g","e"]
    
    # If Player 1 does not play any trump
    if card1.trump == False:
        #print str(card2.trump) + " " + str(card2.face)  
        if card2.trump == True:
            winner = card2
        elif ( (card2.color == card1.color) and (order_of_cards.index(card2.face) > order_of_cards.index(card1.face)) ):
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
    
# Initialize cards

s7 = card("s",7)
s8 = card("s",8)
s9 = card("s",9)
su = card("s","u")
so = card("s","o")
sk = card("s","k")
s10 = card("s",10)
sa = card("s","a")

r7 = card("r",7)
r8 = card("r",8)
r9 = card("r",9)
ru = card("r","u")
ro = card("r","o")
rk = card("r","k")
r10 = card("r",10)
ra = card("r","a")

g7 = card("g",7)
g8 = card("g",8)
g9 = card("g",9)
gu = card("g","u")
go = card("g","o")
gk = card("g","k")
g10 = card("g",10)
ga = card("g","a")

e7 = card("e",7)
e8 = card("e",8)
e9 = card("e",9)
eu = card("e","u")
eo = card("e","o")
ek = card("e","k")
e10 = card("e",10)
ea = card("e","a")

# Reizreihenfolge

reizreihenfolge = [18,20,22,24,27,30,35,36,40,44]

# Define Class Player which stores cards on hand, provides a function to compute possible (lawful) moves, keeps track of won cards, provides function for decision maker (at the moment random)

class player:
    def __init__(self,cards,name):
        self.cards = cards
        self.stiche = []
        self.name = name
    
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
    
    # Reizen
    def reizen(self, history = None, ansage = None):
        if random(1) >= 0.5:
            if ansage == None:
                return 18
            else:
                return reizwert[ansage+1]
        else:
            return False

# Start new game (At the moment the type of game is decided first and then the cards are distributed)

def start_new_game(game,player1,player2,player3,first_player,single_player):
    # Initialize hands of players and Skat, at the moment player 1 starts
    #player1 = player([],"A")
    #player2 = player([],"B")
    #player3 = player([],"C")
    skat =[]
    all_cards = [s7,s8,s9,su,so,sk,s10,sa,r7,r8,r9,ru,ro,rk,r10,ra,g7,g8,g9,gu,go,gk,g10,ga,e7,e8,e9,eu,eo,ek,e10,ea]
    
    # Sitting order of players and first player "dran"
    order_of_players = [player1, player2, player3]
    #first_player = 0
    #single_player = 2
    
    # Distribute cards in the "correct" way
    random.shuffle(all_cards)
    for i in range(len(all_cards)):
        # Distribtion scheme
        if i in [0,1,2,11,12,13,14,23,24,25]:
            player1.hand(all_cards[i])
        if i in [3,4,5,15,16,17,18,26,27,28]:
            player2.hand(all_cards[i])
        if i in [6,7,8,19,20,21,22,29,30,31]:
            player3.hand(all_cards[i])
        if i in [9,10]:
            skat.append(all_cards[i])
    
    # Define trumps and card orders for possible games
    order_of_cards = [7,8,9,"o","k",10,"a"]
    
    if game == "s":
        for card in all_cards:
            if card.color == "s" or card.face == "u":
                card.trump = True
    elif game == "r":
        for card in all_cards:
            if card.color == "r" or card.face == "u":
                card.trump = True
    elif game == "g":
        for card in all_cards:
            if card.color == "g" or card.face =="u":
                card.trump = True
    elif game == "e":
        for card in all_cards:
            if card.color == "e" or card.face == "u":
                card.trump = True
    elif game == "grand":
        for card in all_cards:
            if card.face == "u":
                card.trump = True
    elif game == "null":
        order_of_cards = [7,8,9,10,"u","o","k","a"]
    
    # Give Skat to single player
    
    order_of_players[single_player].stiche = skat
    
    # Go through all rounds and play the game 
    
    dran = first_player
    for i in range(10):
        cards_in_stich = [0,0,0]
        cards_in_stich[dran] = order_of_players[dran].play()
        cards_in_stich[(dran + 1)%3] = order_of_players[(dran + 1)%3].play([cards_in_stich[dran]])
        cards_in_stich[(dran + 2)%3] = order_of_players[(dran + 2)%3].play([cards_in_stich[dran],cards_in_stich[(dran + 1)%3]])
        winner = order_of_players[cards_in_stich.index(stich(cards_in_stich,order_of_cards))]
        winner.win(cards_in_stich)
        dran = order_of_players.index(winner)
    
    # Calculate winner, except for "Null"
    
    punkte = []
    for spieler in order_of_players:
        punkte.append(spieler.points())
    #return order_of_players[punkte.index(max(punkte))]
    return order_of_players
    
    
# Actual execution of game
#ergebnisse = start_new_game("s")
#for spieler in ergebnisse:
#    print  spieler.name + " hat " + str([a.name for a in spieler.stiche]) + " or " + str([a.value for a in spieler.stiche]) + " i.e. " + str(spieler.points())

# Play a series

# Create players
player1 = player([],"A")
player2 = player([],"B")
player3 = player([],"C")

#
