package xswing.ai;

public class AISignal {

	 protected boolean nextTurn = false;

	  public synchronized boolean isNextTurn(){
	    return this.nextTurn;
	  }

	  public synchronized void setNextTurn(boolean nextTurn){
	    this.nextTurn = nextTurn;  
	  }

}
