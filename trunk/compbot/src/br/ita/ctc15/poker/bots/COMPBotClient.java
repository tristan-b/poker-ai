package br.ita.ctc15.poker.bots;

import java.io.*;
import java.net.*;
import java.security.*;

import ca.ualberta.cs.poker.free.client.AdvancedPokerClient;
import ca.ualberta.cs.poker.free.dynamics.Card;

public class COMPBotClient extends AdvancedPokerClient {

    SecureRandom random;
    static double result;
    
    public COMPBotClient(){
    	super();
    	random = new SecureRandom();  
    }
    
    public void handleStateChange() throws IOException, SocketException{
        state.setFromMatchStateMessage(currentGameStateString);
        
        if (state.isOurTurn()){
            takeAction();
        }
    }    
        
    public void takeAction() throws SocketException, IOException{        
        
        if(state.roundIndex == 0)
        	runPreFlopHeuristics(state.hole[state.seatTaken]);
     
        else {
        	double dart = random.nextDouble();
        	
        	if (dart < 0.05){
        		sendFold();
        	
        	} else if (dart < 0.55){
        		sendCall();
        	
        	} else {
        		sendRaise();
        	}
        }
        
        //como tratar o timeout per hand(ms) de 7000? Ž preciso?
        
        result = state.bankroll;
    }
    
    public void runPreFlopHeuristics(Card[] cards) throws SocketException, IOException{
    	
    	//Folding if there's a gap >= 3 AND there's no card >= 10
    	if(Math.abs(cards[0].getIndexRankMajor() - cards[1].getIndexRankMajor()) > 12 
    			&& Math.max(cards[0].getIndexRankMajor(), cards[1].getIndexRankMajor()) < 32)
    		sendFold();

    	//Raising (and re-raising) if both cards are >= J    	
    	if(cards[1].getIndexRankMajor() > 35 && cards[0].getIndexRankMajor() > 35)
    		if(state.roundBets == 1)
    			sendRaise();
    	
    	sendCall();
    }
    
    /**
     * @param args the command line parameters (IP and port)
     */
    public static void main(String[] args) throws Exception{
    	COMPBotClient cbc = new COMPBotClient();
        System.out.println("COMPBot est‡ tentando se conectar a "+args[0]+" na porta "+args[1]+"...");

        cbc.connect(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
        System.out.println("Conex‹o estabelecida!");
        cbc.run();
        
        System.out.printf("\nbankroll: %lf",result);
        System.out.println("Conex‹o encerrada!");
    }	
	
}