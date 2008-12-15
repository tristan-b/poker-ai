package br.ita.ctc15.poker.ai;

import java.io.*;

import br.ita.ctc15.poker.misc.AdvancedClientPokerDynamics;

public class GameTreeSearch {

	private static GameTreeSearch uniqueInstance;
	private double finalEquity;
	
	//Singleton
	public static GameTreeSearch getInstance() {
		if(getUniqueInstance() == null)
			setUniqueInstance(new GameTreeSearch());

		return getUniqueInstance();
	}

	private static GameTreeSearch getUniqueInstance() {
		return uniqueInstance;
	}

	private static void setUniqueInstance(GameTreeSearch uniqueInstance) {
		GameTreeSearch.uniqueInstance = uniqueInstance;
	}	
	
	
	public char runModifiedMinimax(AdvancedClientPokerDynamics dynamics) {
		char lastChar = dynamics.bettingSequence.charAt(dynamics.bettingSequence.length() - 1);
		char lastAction = (lastChar == '/') ? 'i' : (lastChar == 'r' ? 'r' : ((lastChar == 'c' ? 'c' : 'i')));
		
		Node root = buildGameTree(dynamics.seatTaken, dynamics.roundIndex, dynamics.getNumberOfActionsInThisRound('r'), 
				dynamics.inPot[0] + dynamics.inPot[1], lastAction);
		
		double foldFromRootValue = computeModifiedMinimax(root.fold);
		double callFromRootValue = computeModifiedMinimax(root.call);
		double raiseFromRootValue = computeModifiedMinimax(root.raise);

		if (dynamics.getNumberOfActionsInThisRound('r') == 4){
			if (foldFromRootValue > callFromRootValue && foldFromRootValue > raiseFromRootValue)
				return 'f'; 
			else
				return 'c'; 
		}
		else {
			if (foldFromRootValue > callFromRootValue && foldFromRootValue > raiseFromRootValue)
				return 'f';
			else {
				if (callFromRootValue > foldFromRootValue && callFromRootValue > raiseFromRootValue)
					return 'c'; 
				else
					return 'r'; 
			}
		}
	}

	private static Node buildGameTree(int player, int stage, int numberOfRaises, double pot, char action) {
      
		int nextPlayer = (player == 0) ? 1 : 0;
		int bet = (stage == 1) ? 10 : 20;
		Node node = new Node(player, stage, numberOfRaises, pot, action);
      
		if (action == 'f')
			return node;

		//fold      
		node.fold = buildGameTree(nextPlayer, stage, numberOfRaises, pot, 'f');
      
		//call
		if (stage < 3){
			if (numberOfRaises > 0)
	    		node.call = buildGameTree(nextPlayer, stage+1, 0, pot+bet, 'i');
			else
				if (action == 'c')
					node.call = buildGameTree(nextPlayer, stage+1, 0, pot, 'i');
				else 
					node.call = buildGameTree(nextPlayer, stage, numberOfRaises, pot, 'c');
		}
		else
			if (numberOfRaises > 0)
				node.call = buildGameTree(nextPlayer, stage, 0, pot+bet, 'f');
			else
				if (action == 'c')
					node.call = buildGameTree(nextPlayer, stage, 0, pot, 'f');
				else 
					node.call = buildGameTree(nextPlayer, stage, numberOfRaises, pot, 'c');

		//raise
		if (numberOfRaises < 4)
			node.raise = buildGameTree(nextPlayer, stage, numberOfRaises+1, pot+bet, 'r');
	      		
		return node;
	}

	private static double computeModifiedMinimax(Node n) {
		double result;
		
		if (n == null)
			return 0;

		if (n.action == 'f')
			return (n.player == 0) ? -n.pot : +n.pot;
		else
			if (n.player == 0) {
				result = Math.max(-1,computeModifiedMinimax(n.fold));
				
				if (n.call != null) {
					result = Math.max(result,computeModifiedMinimax(n.call));
					if (n.raise != null)
						result = Math.max(result,computeModifiedMinimax(n.raise));
				}
			}
			else {
				double foldValue = computeModifiedMinimax(n.fold);
				double callValue = computeModifiedMinimax(n.call);
				double raiseValue = computeModifiedMinimax(n.raise);
    	  
				result = runOpponentModel(foldValue, callValue, raiseValue);
			}

		return result;
	}
	
	private static double runOpponentModel(double foldValue, double callValue, double raiseValue) {
		return foldValue*0.4 + callValue*0.3 + raiseValue*0.3;
	}
	
	public static double refreshOpponentModel() {
		return 0.0;
	}
}

class Node {
	int deepness, player;
	int stage, numberOfRaises, calls;
	double pot;
	char action;
	Node fold, call, raise;
		
	public Node() {
		super();
	}
   
	public Node(int player, int stage, int numberOfRaises, double pot, char action) {
		super();
		this.player = player;
		this.stage = stage;
		this.numberOfRaises = numberOfRaises;
		this.pot = pot;
		this.action = action;
	}
}
