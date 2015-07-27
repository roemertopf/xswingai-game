package xswing.ai;

import xswing.ball.Ball;

/**
 * This interface is used for communication between the game and agents that play the game.
 * 
 * @author TobiasSebastian
 *
 */
public interface AgentInterface {
	
	/**
	 * Drops the ball that is currently hold by the dropper at the given row.
	 * @param row to drop at
	 * @return 
	 */
	void dropBall(int row);
	
}
