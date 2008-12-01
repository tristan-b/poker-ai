package br.ita.ctc15.poker.misc;

import org.pokersource.enumerate.BeliefVector;
import org.pokersource.enumerate.Enumerate;
import org.pokersource.enumerate.HoldemBeliefVector;
import org.pokersource.enumerate.SAIE;
import org.pokersource.game.Deck;

import ca.ualberta.cs.poker.free.client.ClientPokerDynamics;

public class AdvancedClientPokerDynamics extends ClientPokerDynamics {
    
	double looseOpponentCoefficient;
	
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
		
		for(int i = 0; i < roundIndex + 1; i++)
			if(board[i].getIndexRankMajor() > 35)
				highCards++;
		
		for(int i = 0; i < roundIndex + 1; i++)
			
				highCards++;		
		
		return (0.1*highCards + 0.2*straightDanger + 0.3*flushDanger + 0.4*repeatedCards)*0.2;
	}
	
	public double getOpponentsBet() {
		return 0.0;
	}
	
	public double getOpponentsBluff() {
		return 0.0;
	}	
}
