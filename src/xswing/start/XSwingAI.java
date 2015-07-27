package xswing.start;

import static lib.mylib.options.Paths.RES_DIR;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import com.sun.corba.se.impl.oa.poa.AOMEntry;

import lib.mylib.ScalableGameState;
import lib.mylib.gamestates.LoadingScreen;
import xswing.GamePanel;
import xswing.LocationController;
import xswing.MainGame;
import xswing.ai.AICommunicator;
import xswing.ai.AIListener;
import xswing.ai.AgentInterface;
import xswing.ball.Ball;

/**
 * Starts the game in a special way, so that the AI can play the game via the interface.
 * 
 * @author TobiasSebastian
 *
 */
public class XSwingAI extends StateBasedGame implements Runnable{
	
	public AgentInterface game;
	private AIListener agent;
	private AICommunicator communicator;
	
	public XSwingAI(AICommunicator comm) {
		super("XSwingAI");
		this.communicator = comm;
	}
	
	@Override
	public void run() {
		// starting the game when thread is running this class
		startGame();
	}
	
	/**
	 * Starts the game in ai mode with some hard-coded settings.
	 */
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

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		LocationController.setMultiplayer(false);
		addState(new LoadingScreen(0));
		//addState(new GamePanel(1));
		
		MainGame mainGame = new MainGame();
		mainGame.setAICommunicator(communicator);
		container.setClearEachFrame(true);
		ScalableGameState scaledGame = new ScalableGameState(mainGame, 1920, 1080, true);
		scaledGame.setId(1);
		game = (AgentInterface) mainGame;
		
		addState(scaledGame);
		// ResizeableGameState scaledGamePan
	}

}
