package br.ita.ctc15.poker.bots;

import java.io.*;
import java.net.*;
import java.security.*;

import br.ita.ctc15.poker.misc.AdvancedClientPokerDynamics;
import br.ita.ctc15.poker.ai.GameTreeSearch;
import br.ita.ctc15.poker.ai.Heuristics;

import ca.ualberta.cs.poker.free.client.AdvancedPokerClient;

public class GameTreeSearchBot extends AdvancedPokerClient {
    
    public GameTreeSearchBot(){
    	dynamics = new AdvancedClientPokerDynamics();
    }
    
    public void handleStateChange() throws IOException, SocketException{
        dynamics.setFromMatchStateMessage(currentGameStateString);
        
        if (dynamics.isOurTurn()){
            takeAction();
        }
    }    
        
    public void takeAction() throws SocketException, IOException{        

        if(dynamics.roundIndex == 0)
        	sendAction(Heuristics.getInstance().preFlopBasedInGMGroups(dynamics));        	

        else if(dynamics.roundIndex < 4)
        	sendAction(GameTreeSearch.getInstance().runModifiedMinimax(dynamics));
        
        else
        	GameTreeSearch.getInstance().refreshOpponentModel();
    }

    
    /**
     * @param args the command line parameters (IP and port)
     */
    public static void main(String[] args) throws Exception{
    	HeuristicBotClient cbc = new HeuristicBotClient();
        System.out.println("GameTreeSearchBot est‡ tentando se conectar a " + args[0] + " na porta " + args[1] + "...");

        cbc.connect(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
        System.out.println("Conex‹o estabelecida!");
        cbc.run();
        
        System.out.println("Conex‹o encerrada!");
    }	
	
}
