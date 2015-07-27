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
	 * Returns the current ball depot in a two dimensional array. 
	 * The depot is located at the top of the screen during play and provides new balls, that can be taken.
	 * The array is [row][column] with column 0 being the lower column from where the balls may be taken and column 1
	 * the upper one. Rows are between 0 and 12.
	 * @return current ball depot
	 */
	Ball[][] getBallDepot();
	
	/**
	 * Returns the current game board in a two dimensional array.
	 * On the game board all balls are placed in 8 rows and 8 columns. The array is [row][column] with [0][0] in
	 * the bottom left corner of the game board.
	 * @return current game board
	 */
	Ball[][] getGameboard();
	
	/**
	 * Returns the ball currently hold by the dropper.
	 * @return current ball
	 */
	Ball getBall();
	
	/**
	 * Drops the ball that is currently hold by the dropper at the given row.
	 * @param row to drop at
	 * @return 
	 */
	void dropBall(int row);
	
}
