/*
 * AdvancedPokerClient.java
 *
 * Created on April 19, 2006, 10:19 AM
 */

package ca.ualberta.cs.poker.free.client;

import java.io.IOException;
import java.net.SocketException;

import br.ita.ctc15.poker.misc.AdvancedClientPokerDynamics;

/**
 * An extension of PokerClient that contains a reference to a reproduction of what is happening on 
 * the server side (state).
 * Can overload takeAction() (instead of handleStateChange()) to only receive messages when it is
 * your turn to act.
 *
 * As before, actions can be taken with sendFold, sendCall(), and sendRaise()
 * @author Martin Zinkevich
 */
public class AdvancedPokerClient extends PokerClient{
    /**
     * A reproduction of what is happening on the server side.
     */
    public AdvancedClientPokerDynamics dynamics;
    
    /** 
     * Handles the state change. 
     * Updates state and calls takeAction()
     */
    public void handleStateChange() throws IOException, SocketException{
        dynamics.setFromMatchStateMessage(currentGameStateString);
        if (dynamics.isOurTurn()){
            takeAction();
        }
    }
    /** 
     * Creates a new instance of AdvancedPokerClient. Must call connect(), then run() to start process 
     */
    public AdvancedPokerClient(){
        dynamics = new AdvancedClientPokerDynamics();
    }
    
    /**
     * Overload to take actions.
     */
    public void takeAction() throws SocketException, IOException{
        try{
            if (dynamics.roundBets<4){
                if (Math.random()<0.5){
                    sendRaise();
                }  else {
                    sendCall();
                }
            } else {
              sendCall();
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
