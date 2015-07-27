package xswing.ai;

import xswing.ball.Ball;

public class AICommunicator {
	
	private int dropAt;
	
	private static final int row = 10;
	private static final int column = 8;
	private static final int depotStartPos = 8;
	private static final int depotSize = 2;
	
	public class AIBall{
		/** The color of the ball. Value range: 1 - 49 */
		public int color = 0;
		/** The weight of the ball. Value range:  */
		public int weight = 0;
		
		@Override
		public String toString() {
			return "AIBall [color=" + color + ", weight=" + weight + "]";
		}
		
	}
	
	private AIBall[][] ballTable;
	
	public AICommunicator(){
		ballTable = new AIBall[row][column];
		for (int i = 0; i < row; i++){
			for (int j = 0; j < column; j++){
				ballTable[i][j] = new AIBall();
			}
		}
	}
	
	public synchronized void updateBoard(Ball[][] ballTable){
		
	}
	
	/**
	 * Returns the current ball depot in a two dimensional array. 
	 * The depot is located at the top of the screen during play and provides new balls, that can be taken.
	 * The array is [row][column] with column 0 being the lower row from where the balls may be taken and row 1
	 * the upper one. Columns are between 0 and 8.
	 * @return current ball depot
	 */
	public AIBall[][] getBallDepot(){
		AIBall[][] depot = new AIBall[2][8];
		// depot is in upper section of array ballTable
		for (int i = 0; i < depotSize; i++){
			for (int j = 0; j < column; j++){
				depot[i][j] = ballTable[i+depotStartPos][j];
			}
		}
		return depot;
	}
	
	/**
	 * Returns the current game board in a two dimensional array.
	 * On the game board all balls are placed in 8 rows and 8 columns. The array is [row][column] with [0][0] in
	 * the bottom left corner of the game board.
	 * @return current game board
	 */
	public Ball[][] getGameboard(){
		return null;
	}
	
	/**
	 * Returns the ball currently hold by the dropper.
	 * @return current ball
	 */
	public Ball getBall(){
		return null;
	}

	public int getDropAt() {
		int val = dropAt;
		dropAt = -1;
		return val;
	}

	public void setDropAt(int dropAt) {
		this.dropAt = dropAt;
	}

}
