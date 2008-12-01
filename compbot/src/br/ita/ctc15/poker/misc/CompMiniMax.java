package br.ita.ctc15.poker.misc;

import java.io.*;

public class CompMiniMax
{
   public static void main (String [] args)
   {
      // Build a game tree to keep track of all possible game configurations
      // that could occur during games of Nim with an initial pile of four
      // matches. The first move is made by player A.

	  int raises = 3;
      Node root = buildGameTree ('A',1,raises,40,'R');

      // Use the minimax algorithm to determine if player A's optimal move is
      // the child node to the left of the current root node, the child node
      // directly below the current root node, or the child node to the right
      // of the current root node.

      double v1 = computeMinimax (root.left);
      double v2 = computeMinimax (root.center);
      double v3 = computeMinimax (root.right);

      if (raises == 4){
    	  if (v1 > v2 && v1 > v3)
              System.out.println ("Move to the left node.");
          else
              System.out.println ("Move to the center node."); 
      }else{
    	  if (v1 > v2 && v1 > v3)
    		  System.out.println ("Move to the left node.");
    	  else
    		  if (v2 > v1 && v2 > v3)
    			  System.out.println ("Move to the center node.");
    		  else
    			  System.out.println ("Move to the right node.");
      	}
   }

   static Node buildGameTree (char player,int stage,int raises,int pot, char action)
   {
      char nextPlayer = (player == 'A') ? 'B' : 'A';
      int bet = (stage == 1) ? 10 : 20;
      
	  Node n = new Node(player,stage,raises,pot,action);
      if (action == 'F')
    	  return n;

      //fold      
    	  n.left = buildGameTree (nextPlayer, stage, raises, pot, 'F');
      
      //call
      if (stage < 3){
	      if (raises > 0)
	    	  n.center = buildGameTree (nextPlayer, stage+1, 0, pot+bet, 'I');
	      else
	    	  if (action == 'C')
	    		  n.center = buildGameTree (nextPlayer, stage+1, 0, pot, 'I');
	    	  else 
	    		  n.center = buildGameTree (nextPlayer, stage, raises, pot, 'C');
      }
      else
    	  if (raises > 0)
	    	  n.center = buildGameTree (nextPlayer, stage, 0, pot+bet, 'F');
	      else
	    	  if (action == 'C')
	    		  n.center = buildGameTree (nextPlayer, stage, 0, pot, 'F');
	    	  else 
	    		  n.center = buildGameTree (nextPlayer, stage, raises, pot, 'C');


      //raise
      if (raises < 4)
    	  n.right = buildGameTree (nextPlayer, stage, raises+1, pot+bet, 'R');
	      
      
      /*Node n = new Node ();
      n.nmatches = nmatches;
      n.player = player;

      if (nmatches >= 1)
          n.left = buildGameTree (nmatches-1, );
      if (nmatches >= 2)
          n.center = buildGameTree (nmatches-2, (player == 'A') ? 'B' : 'A');
      if (nmatches >= 3)
          n.right = buildGameTree (nmatches-3, (player == 'A') ? 'B' : 'A');
	*/
      return n;
   }

   static double computeMinimax (Node n)
   {
	  if (n == null) return 0;
	   
      double ans;

      if (n.action == 'F')
          return (n.player == 'A') ? -n.pot : +n.pot;
      else
      if (n.player == 'A')
      {
          ans = Math.max (-1, computeMinimax (n.left));
          if (n.center != null)
          {
              ans = Math.max (ans, computeMinimax (n.center));
              if (n.right != null)
                  ans = Math.max (ans, computeMinimax (n.right));
          }
      }
      else
      {
    	  double foldProbability = computeMinimax (n.left);
    	  double callProbability = computeMinimax (n.center);
    	  double raiseProbability = computeMinimax (n.right);
    	  
          ans = foldProbability*0.4 + callProbability*0.3 + raiseProbability*0.3;
    	  //ans = callProbability*0.5 + raiseProbability*0.3;
          	  
    	  /*ans = Math.min (1, computeMinimax (n.left));
          if (n.center != null)
          {
              ans = Math.min (ans, computeMinimax (n.center));
              if (n.right != null)
                  ans = Math.min (ans, computeMinimax (n.right));
          }*/
      }

      return ans;
   }
}

class Node
{
   int nmatches; // Number of matches remaining after a move to this Node
                 // from the parent Node.
   char player;  // Game configuration from which player (A - player A, B -
                 // player B) makes a move.
   int stage, raises, calls, pot;
   
   char action;
   
   Node left;    // Link to left child Node -- a move is made to left Node
                 // when 1 match is taken. (This link is only null when the
                 // current Node is a leaf.)
   Node center;  // Link to center child Node -- a move is made to this Node
                 // when 2 matches are taken. (This link may be null, even if
                 // the current Node is not a leaf.)
   Node right;   // Link to right child Node -- a move is made to this Node
                 // when three matches are taken. (This link may be null,
                 // even if the current Node is not a leaf.)
	public Node() {
		super();
	}
   
   public Node(char player, int stage, int raises, int pot, char action) {
		super();
		this.player = player;
		this.stage = stage;
		this.raises = raises;
		this.pot = pot;
		this.action = action;
	}
}

