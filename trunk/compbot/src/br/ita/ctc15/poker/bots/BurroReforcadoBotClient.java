package br.ita.ctc15.poker.bots;

import java.io.*;
import java.net.*;
import java.security.*;

import ca.ualberta.cs.poker.free.client.AdvancedPokerClient;

public class BurroReforcadoBotClient extends AdvancedPokerClient {

    SecureRandom random;
    static double result;
    
    public BurroReforcadoBotClient(){
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
                
        //como tratar o timeout per hand(ms) de 7000? Ž preciso?
        
        result = state.bankroll;
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