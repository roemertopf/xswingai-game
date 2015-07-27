package xswing.ai;

/**
 * AIListener for proposed for agents. They have to implement this listener interface
 * in order to get informed that the game is ready for playing.
 * 
 * @author TobiasSebastian
 *
 */
public interface AIListener {
	
	void gameStarted();
	
	// maybe also
	// void nextTurn();
}
