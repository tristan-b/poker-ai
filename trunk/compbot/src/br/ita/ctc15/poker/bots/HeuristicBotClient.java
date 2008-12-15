package br.ita.ctc15.poker.bots;

import java.io.*;
import java.net.*;
import java.security.*;

import br.ita.ctc15.poker.misc.AdvancedClientPokerDynamics;
import br.ita.ctc15.poker.ai.Heuristics;

import ca.ualberta.cs.poker.free.client.AdvancedPokerClient;

public class HeuristicBotClient extends AdvancedPokerClient {
    
    public HeuristicBotClient(){
    	dynamics = new AdvancedClientPokerDynamics();
    }
    
    public void handleStateChange() throws IOException, SocketException{
        dynamics.setFromMatchStateMessage(currentGameStateString);
        
        if (dynamics.isOurTurn()){
            takeAction();
        }
    }    
        
    public void takeAction() throws SocketException, IOException{        

        if(dynamics.roundIndex == 0){
        	dynamics.assumedStrength = null;
        	sendAction(Heuristics.getInstance().preFlopBasedInGMGroups(dynamics));
        }
        	
        else if(dynamics.roundIndex < 4)
        	sendAction(Heuristics.getInstance().posFlopBasedInEVAndSAIE(dynamics));
        
        else{
        	dynamics.refreshOpponentLooseness();
        }
        if(dynamics.roundIndex == 4){
        	System.out.println("------------------------------ SHOWDOWN ------------------------------");
        	System.out.println(dynamics.bettingSequence + "::::" + dynamics.getCardState(2));
        	System.out.println("----------------------------------------------------------------------");
        }
        //como tratar o timeout per hand(ms) de 7000? Ž preciso?
    }

    
    /**
     * @param args the command line parameters (IP and port)
     */
    public static void main(String[] args) throws Exception{
    	HeuristicBotClient cbc = new HeuristicBotClient();
        System.out.println("HeuristicBot est‡ tentando se conectar a " + args[0] + " na porta " + args[1] + "...");

        cbc.connect(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
        System.out.println("Conex‹o estabelecida!");
        cbc.run();
        
        System.out.println("Conex‹o encerrada!");
    }	
	
}