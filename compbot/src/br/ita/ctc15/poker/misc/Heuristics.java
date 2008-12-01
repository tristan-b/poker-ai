package br.ita.ctc15.poker.misc;

import java.security.SecureRandom;

import org.pokersource.enumerate.HoldemSMGroup;
import org.pokersource.game.Deck;

import ca.ualberta.cs.poker.free.dynamics.Card;

public class Heuristics {

    private SecureRandom fuzzy;
	private static Heuristics uniqueInstance;
	
	//Singleton
	public static Heuristics getInstance() {
		if(getUniqueInstance() == null)
			setUniqueInstance(new Heuristics());

		return getUniqueInstance();
	}

	private static Heuristics getUniqueInstance() {
		return uniqueInstance;
	}

	private static void setUniqueInstance(Heuristics uniqueInstance) {
		Heuristics.uniqueInstance = uniqueInstance;
	}	
	
	//Pre-flop
	public char preFlopBasedInGMGroups(AdvancedClientPokerDynamics dynamics){
		
    	HoldemSMGroup gSM1 = new HoldemSMGroup("SM1");
    	HoldemSMGroup gSM2 = new HoldemSMGroup("SM2");
    	HoldemSMGroup gSM3 = new HoldemSMGroup("SM3");
    	HoldemSMGroup gSM4 = new HoldemSMGroup("SM4");
    	HoldemSMGroup gSM5 = new HoldemSMGroup("SM5");
    	HoldemSMGroup gSM6 = new HoldemSMGroup("SM6");
    	HoldemSMGroup gSM7 = new HoldemSMGroup("SM7");
    	HoldemSMGroup gSM8 = new HoldemSMGroup("SM8");
    	HoldemSMGroup gSM9 = new HoldemSMGroup("SM9");
    	long longCards = Deck.parseCardMask("" + dynamics.hole[dynamics.seatTaken][0] + dynamics.hole[dynamics.seatTaken][1]);
    	
    	if(gSM9.isHandInGroup(longCards))
    		return 'f';
    	else if(gSM1.isHandInGroup(longCards) || gSM2.isHandInGroup(longCards) || gSM3.isHandInGroup(longCards) || gSM4.isHandInGroup(longCards))
    		return 'r';
    	else if(gSM5.isHandInGroup(longCards) || gSM6.isHandInGroup(longCards) || gSM7.isHandInGroup(longCards) || gSM8.isHandInGroup(longCards))
    		return 'c';
		
    	return 'f';
	}

	//Pos-flop
	public char posFlopBasedInEVAndSAIE(AdvancedClientPokerDynamics dynamics){
		
		double victoryBelief = dynamics.getVictoryBeliefAfterPreFlop();
		double expectedValue = dynamics.getExpectedValueAfterPreFlop();
		
		if(victoryBelief  > 0.7)
			return 'r';
		else if(victoryBelief > 0.5)
			return 'c';
		else {
			if(expectedValue > 0)
				return 'c';
			else
				return 'f';
		}
		
	}	
	
}


