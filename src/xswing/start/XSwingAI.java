package xswing.start;

import static lib.mylib.options.Paths.RES_DIR;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import lib.mylib.ScalableGameState;
import lib.mylib.gamestates.LoadingScreen;
import xswing.GamePanel;
import xswing.LocationController;
import xswing.MainGame;
import xswing.ai.AIListener;
import xswing.ai.AgentInterface;
import xswing.ball.Ball;

/**
 * Starts the game in a special way, so that the AI can play the game via the interface.
 * 
 * @author TobiasSebastian
 *
 */
public class XSwingAI extends StateBasedGame implements AgentInterface{
	
	private MainGame mainGame;
	private AIListener agent;
	
	public XSwingAI(AIListener agent) {
		super("XSwingAI");
		this.agent = agent;
	}
	
	public void startGame(){
		boolean fullscreen = false;
		try {
			AppGameContainer game = new AppGameContainer(this);
			game.setShowFPS(true);
			// MIt Wireframe:
			game.setDisplayMode(1280, 720, fullscreen);
			game.setClearEachFrame(true);
			game.setIcons(new String[] { RES_DIR + "16.png", RES_DIR + "32.png" });
			game.setForceExit(false);
			game.setMouseGrabbed(false);
			game.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
	}

//	/**
//	 * @param args [0] debugmode (true= window mode, no mouse grabbing, debuginfos, show fps)
//	 */
//	public static void main(String[] args) {
//		boolean debug = false;
//		if (args.length > 0) {
//			debug = Boolean.valueOf(args[0]);
//		}
//		boolean fullsceen = !debug;
//		Log.info("Debugmode: " + debug);
//		// Log.setVerbose(debug); //debug info logging
//		try {
//			AppGameContainer game = new AppGameContainer(new XSwingFastStart());
//			game.setShowFPS(debug);
//			game.setDisplayMode(1024, 768, fullsceen);
//			// game.setDisplayMode(460,390,fullsceen);
//			game.setClearEachFrame(true);
//			game.setIcons(new String[] { RES_DIR + "16.png", RES_DIR + "32.png" });
//			game.setForceExit(false);
//			game.setMouseGrabbed(!debug);
//			game.start();
//		} catch (SlickException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		LocationController.setMultiplayer(false);
		addState(new LoadingScreen(0));
		//addState(new GamePanel(1));
		
		mainGame = new MainGame();
		mainGame.addAIListener(agent);
		container.setClearEachFrame(true);
		ScalableGameState scaledGame = new ScalableGameState(mainGame, 1920, 1080, true);
		scaledGame.setId(1);
		
		addState(scaledGame);
		// ResizeableGameState scaledGamePan
	}

	@Override
	public Ball[][] getBallDepot() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Ball[][] getGameboard() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Ball getBall() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void dropBall(int row) {
		mainGame.dropBall(row);		
	}

	

}
