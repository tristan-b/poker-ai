package br.ita.ctc15.poker.misc;

import org.pokersource.enumerate.BeliefVector;
import org.pokersource.enumerate.Enumerate;
import org.pokersource.enumerate.HoldemBeliefVector;
import org.pokersource.enumerate.SAIE;
import org.pokersource.game.Deck;

import ca.ualberta.cs.poker.free.client.ClientPokerDynamics;
import ca.ualberta.cs.poker.free.dynamics.Card;

public class AdvancedClientPokerDynamics extends ClientPokerDynamics {
    
	double looseOpponentCoefficient = 0;
	
	public double getExpectedValueAfterPreFlop() {
		return (inPot[0] + inPot[1]) * getVictoryBeliefAfterPreFlop() + 
			((roundIndex == 0 && seatTaken == 1) ? 5.0 : (roundIndex >= 2) ? 20.0 : 10.0)*(1 - getVictoryBeliefAfterPreFlop());  
	}
	
	public double getVictoryBeliefAfterPreFlop() {
		HoldemBeliefVector heroHand;
		HoldemBeliefVector opponentHand;
		long board;
		double equity[] = new double[2];

		heroHand = new HoldemBeliefVector("" + hole[seatTaken][0] + hole[seatTaken][1]);
		opponentHand = new HoldemBeliefVector("SM1 SM2 SM3 SM4 SM5 SM6 SM7 SM8 SM9");	//any card	
		
		//Flop
		if(roundIndex == 1) {
			board = Deck.parseCardMask(this.board[0] + " " + this.board[1] + " " + this.board[2]); 
		    SAIE.FlopGameSAIE(Enumerate.GAME_HOLDEM, 0, 0, new BeliefVector[] {heroHand, opponentHand}, board, 0, equity, null);
		    return (equity[0] - getOpponentHandStrength())*0.5 + 0.5;
		}

		//Turn
		else if(roundIndex == 2) {		
			board = Deck.parseCardMask(this.board[0] + " " + this.board[1] + " " + this.board[2] + " " + this.board[3]); 
		    SAIE.FlopGameSAIE(Enumerate.GAME_HOLDEM, 0, 0, new BeliefVector[] {heroHand, opponentHand}, board, 0, equity, null);
		    return (equity[0] - getOpponentHandStrength())*0.5 + 0.5;
		}
		
		//River
		else if(roundIndex == 3) {
			board = Deck.parseCardMask(this.board[0] + " " + this.board[1] + " " + this.board[2] + " " + this.board[3] + " " + this.board[4]); 
		    SAIE.FlopGameSAIE(Enumerate.GAME_HOLDEM, 0, 0, new BeliefVector[] {heroHand, opponentHand}, board, 0, equity, null);
		    return (equity[0] - getOpponentHandStrength())*0.5 + 0.5;
		}
		
		return 0.0;
	}
	
	public double getOpponentHandStrength() {
		return getBoardDanger() * getOpponentsBet() / getOpponentsBluff();
	}	
	
	public double getBoardDanger() {
		int highCards = 0; 
		int straightDanger = 0;
		int flushDanger = 0;
		int repeatedCards = 0;
		int[] straightMask = new int[5];
		int tmp;
		
		for(int i = 0; i <= roundIndex + 1; i++)
			if(board[i].getIndexRankMajor() > 35)
				highCards++;
		
		for(int i = 0; i <= roundIndex; i++)
			for(int j = i + 1; i <= roundIndex + 1; i++){
				if(board[i].suit.index == board[j].suit.index)
					flushDanger++;
				if(board[i].rank.index == board[j].rank.index)
					repeatedCards++;
			}

		straightMask[0] = Card.Rank.ACE.index;
		straightMask[1] = Card.Rank.TWO.index;
		straightMask[2] = straightMask[1] + 1;
		straightMask[3] = straightMask[1] + 2;
		straightMask[4] = straightMask[1] + 3;
		
		for(int i = 0; i < 7; i++){
			tmp = 0;
			
			if(i == 1){
				straightMask[0] = Card.Rank.TWO.index;
				straightMask[1] = straightMask[0] + 1;
				straightMask[2] = straightMask[0] + 2;
				straightMask[3] = straightMask[0] + 3;
				straightMask[4] = straightMask[0] + 4;				
			}
			else
				for(int j = 0; j < 5; j++)
					straightMask[j]++;
			
			for(int j = 0; j < 5; j++)
				for(int k = 0; k <= roundIndex + 1; k++)
					if(straightMask[j] == board[i].rank.index)
						tmp++;
			
			straightDanger = Math.max(tmp, straightDanger);
		}
			
		if(roundIndex == 3 && flushDanger < 3)
			flushDanger = 0;

		if(roundIndex == 3 && straightDanger < 3)
			straightDanger = 0;		
		
		return (0.1*highCards + 0.2*straightDanger + 0.3*flushDanger + 0.4*repeatedCards)*0.2;
	}
	
	public double getOpponentsBet() {
		return (bettingSequence.substring(bettingSequence.length() - 2, bettingSequence.length() - 1).equals("/c")) ? 0.5 : 
			bettingSequence.length() - 1 == 'c' ? 0.75 : 1.0;
	}
	
	public double getOpponentsBluff() {
		return Math.tan((Math.PI/2)*looseOpponentCoefficient) + 1;
	}
	
	public void refreshOpponentLooseness(){
		looseOpponentCoefficient = 0;
	}
}
