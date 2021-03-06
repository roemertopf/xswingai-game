/*
 * @version 0.0 14.04.2008
 * @author Tobse F
 */
package xswing;

import static lib.mylib.options.Paths.FONT_DIR;
import static lib.mylib.options.Paths.MUSIC_DIR;
import static lib.mylib.options.Paths.RES_DIR;
import static lib.mylib.options.Paths.SOUND_DIR;
import static xswing.properties.XSGameConfigs.getConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheetFont;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import lib.mylib.Sound;
import lib.mylib.SpriteSheet;
import lib.mylib.hacks.NiftyGameState;
import lib.mylib.highscore.HighScoreTable;
import lib.mylib.object.BasicGameState;
import lib.mylib.object.Reset;
import lib.mylib.object.Resetable;
import lib.mylib.object.SObject;
import lib.mylib.object.SObjectList;
import lib.mylib.properties.GameConfig;
import lib.mylib.properties.ObjectConfig;
import lib.mylib.util.Clock;
import lib.mylib.util.MusicJukebox;
import lib.mylib.util.SlickUtils;
import tools.BallDropSimulator;
import xswing.EffectCatalog.particleEffects;
import xswing.LocationController.GameComponentLocation;
import xswing.ai.AICommunicator;
import xswing.ai.AIInterface;
import xswing.ai.AIListener;
import xswing.ai.AgentInterface;
import xswing.ball.Ball;
import xswing.ball.BallFactory;
import xswing.ball.BallKiller;
import xswing.ball.BallTable;
import xswing.ball.Mechanics;
import xswing.events.BallEvent;
import xswing.events.BallEvent.BallEventType;
import xswing.events.BallEventListener;
import xswing.events.XSwingEvent;
import xswing.events.XSwingEvent.GameEventType;
import xswing.events.XSwingListener;
import xswing.gui.ScreenControllerScore;
import xswing.properties.ConfigToObjectMapper;
import xswing.properties.ResourcesLoader;
import xswing.start.XSwing;

/**
 * The main container class, which combines all container elements
 * 
 * @author Tobse
 */
public class MainGame extends BasicGameState implements Resetable, BallEventListener, XSwingListener,
	AgentInterface{


	private Background background = new Background();
	private GameComponentLocation gameLocation;
	private GameContainer container = null;
	private StateBasedGame game = null;
	/** Folder with resources */
	public static final String HIGH_SCORE_FILE = XSwing.class.getSimpleName() + "_high_score.hscr";
	private int keyLeft = Input.KEY_LEFT, keyRight = Input.KEY_RIGHT, keyDown = Input.KEY_DOWN;
	private int controllerID = 0;
	// private LocationController locationController;
	private EffectCatalog effectCatalog;
	private Cannon canon;
	private Clock clock;
	private BallTable ballTable;
	private Mechanics mechanics;
	private SeesawTable seesawTable;
	private BallCounter ballCounter;
	private HighScoreTable scoreTable;
	private HighScoreCounter highScoreCounter;
	private HighScoreMultiplicator multiplicator;
	private HighScorePanel highScore;
	private BallKiller ballKiller;
	private Level levelBall;
	private Reset reset;
	private SObjectList ballsToMove;
	private SObjectList gui;
	private BallFactory ballFactory;
	private SObjectList scorePopups;
	private LocalXSwingStatistics statistics;
	private GameOver gameOver= new GameOver();

	private SpriteSheet balls1, balls2, multipl, cannons;
	private SpriteSheetFont numberFont, ballFont;
	private AngelCodeFont fontText, fontScore, pauseFont;
	private MusicJukebox music;
	private Pause pause;

	/** Toggles if the {@link #highScoreState} should be drawn */
	private boolean isGameOver = false;
	private AIInterface ai;
	private BallDropSimulator ballDropSimulator;
	private GameStatistics gameStatistics;

	private EventListenerList gameEventListeners=  new EventListenerList();
	
	/* AI Listener*/
	private AIListener agent;
	private AICommunicator aiCommunicator;

	/** Highscore submit Panel */
	private NiftyGameState highScoreState = null;

	private ScreenControllerScore scoreScreenController;

	private final int startLevel = 4;
	private GameConfig config;
	private Map<String, ObjectConfig> objectStore;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.container = container;
		config = getConfig();
		objectStore = ResourcesLoader.getObjectStore(config.getSelctedObjectConfigSet());

		map(new Ball(0));// Load Ball.A

		ResourcesLoader.accesAllResources(config);

		// Images
		multipl = new SpriteSheet(new Image(RES_DIR + "multiplicator_sp.jpg"), 189, 72);
		cannons = new SpriteSheet(new Image(RES_DIR + "cannons.png"), 72, 110);

		// Fonts
		fontText = new AngelCodeFont(FONT_DIR + "font_arial_16_bold.fnt", FONT_DIR + "font_arial_16_bold.png");
		pauseFont = new AngelCodeFont(FONT_DIR + "arial_black_71.fnt", FONT_DIR + "arial_black_71.png");
		fontScore = new AngelCodeFont(FONT_DIR + "berlin_sans_fb_demi_38.fnt", FONT_DIR + "berlin_sans_fb_demi_38.png");
		numberFont = new SpriteSheetFont(new SpriteSheet(new Image(FONT_DIR + "numberFont_s19.png"), 15, 19), ',');
		ballFont = new SpriteSheetFont(new SpriteSheet(new Image(FONT_DIR + "spriteFontBalls2.png"), 11, 16), '.');
		// Sounds
		// klack1 = new Sound(SOUND_DIR + "KLACK4.WAV");
		// kran1 = new Sound(SOUND_DIR + "KRAN1.WAV");
		// wup = new Sound(SOUND_DIR + "DREIER.WAV");
		// shrinc = new Sound(SOUND_DIR + "SPRATZ2.WAV");
		// warning = new Sound(SOUND_DIR + "ALARM1.WAV");
		// klack1.setMaxPlyingTime(5);
		// wup.setMaxPlyingTime(1000);
		// shrinc.setMaxPlyingTime(5);
		// PropertiesEnum<Args> properties = new PropertiesEnum<Args>(new File("options.ini"));
		// properties.loadINI();
		// String[] musicFiles = properties.getPropertyString(Args.musicFiles).split(";");
		music = new MusicJukebox();
		for (String musicFile : config.getMusicPlayList()) {
			music.addMusic(new Music(MUSIC_DIR + musicFile.trim(), true));
		}
		SoundStore.get().setSoundVolume(config.getSoundConfig().getFxVoulme() / 100f);
		SoundStore.get().setMusicVolume(config.getSoundConfig().getMusicVolume() / 100f);

		// music = new Music(MUSIC_DIR + "music.mod", true);
		// Objects
		map(background);

		addXSwingListener(this);
		gameStatistics = new GameStatistics();
		addXSwingListener(gameStatistics);
		// locationController = new LocationController(gameLocation);

		ballsToMove = new SObjectList();
		gui = new SObjectList();
		effectCatalog = new EffectCatalog();
		reset = new Reset();
		ballTable = new BallTable();
		ballTable.addBallEventListerner(this);
		map(ballTable);
		// locationController.setLocationToObject(ballTable);
		mechanics = new Mechanics(ballTable);
		mechanics.addBallEventListener(this);
		clock = new Clock(numberFont);
		map(clock);
		ballCounter = new BallCounter(ballFont);
		map(ballCounter);
		levelBall = new Level(startLevel, balls1, ballFont);
		map(levelBall);
		ballCounter.setLevel(levelBall);
		canon = new Cannon(cannons, ballTable, ballCounter, effectCatalog);
		map(canon);

		multiplicator = new HighScoreMultiplicator(multipl);
		map(multiplicator);
		highScoreCounter = new HighScoreCounter(numberFont, multiplicator);
		map(highScoreCounter);

		scoreTable = new HighScoreTable();
		scoreTable.load();
		highScore = new HighScorePanel(fontText, scoreTable);
		map(highScore);

		statistics = new LocalXSwingStatistics();
		addXSwingListener(statistics);
		seesawTable = new SeesawTable(numberFont, ballTable);
		map(seesawTable);
		
		map(gameOver);

		setSound(effectCatalog, particleEffects.EXPLOSION);
		setSound(effectCatalog, particleEffects.SHRINC);
		setSound(effectCatalog, particleEffects.BOUNCING);

		ballKiller = new BallKiller(mechanics, highScoreCounter, ballTable);
		ballTable.addBallEventListerner(ballKiller);
		List<SpriteSheet> ballSets = new ArrayList<SpriteSheet>(5);
		ballFactory = new BallFactory(ballTable, ballsToMove, ballFont, ballSets, effectCatalog, canon, levelBall);
		ballFactory.addBallEventListener(this);
		ballFactory.addBallEventListener(gameStatistics);
		ObjectConfig ballFactoryConf = getConf(ballFactory);
		for(String image :ballFactoryConf.getImages().values()){
			ballSets.add(new SpriteSheet(new Image(RES_DIR + image), Ball.A, Ball.A));
		}
		
		scoreScreenController = new ScreenControllerScore(game, scoreTable, clock, gameStatistics);
		scorePopups = new SObjectList();

		pause = new Pause(pauseFont, container.getWidth(), container.getHeight());
		pause.setVisible(false);
		reset.add(gameStatistics);
		reset.add(ballTable);
		reset.add(gui);
		reset.add(ballsToMove);
		reset.add(scorePopups);
		reset.add(effectCatalog);

		gui.add(background);
		gui.add(canon);
		gui.add(clock);
		gui.add(seesawTable);
		gui.add(levelBall);
		gui.add(ballCounter);
		gui.add(multiplicator);
		gui.add(highScoreCounter);
		gui.add(highScore);

		boolean activateAI = false;
		if (activateAI && gameLocation == GameComponentLocation.CENTER) {
			ai = new AIInterface(this, ballTable, canon);
		}
		Log.warn("MainGame..............");
		highScoreState = new NiftyGameState(XSwing.GAME_OVER);
		highScoreState.init(container, game);
		highScoreState.enableMouseImage(new Image("res/cursor.png"), 2, 2);
		// highScoreState.setInput(game.getContainer().getInput());
		container.getInput().removeListener(highScoreState);
		container.getInput().addListener(highScoreState);
	}

	public void map(SObject object) throws SlickException {
		ObjectConfig config = objectStore.get(object.getClass().getSimpleName());
		if(config==null){
			throw new IllegalArgumentException("Coul't fin a config for "+object.getClass().getName()+":" +object);
		}
		ConfigToObjectMapper.map(object, config);
	}

	private void setSound(EffectCatalog effects, particleEffects effect) throws SlickException {
		ObjectConfig config = objectStore.get(EffectCatalog.class.getSimpleName());
		effects.setSound(new Sound(SOUND_DIR + config.getSound(effect.toString().toLowerCase())), effect);
	}

	public ObjectConfig getConf(SObject sObject) {
		return objectStore.get(sObject.getClass().getSimpleName());
	}

	public void setKeys(int keyCodeLeft, int keyCodeRight, int keyCodeDown) {
		keyLeft = keyCodeLeft;
		keyRight = keyCodeRight;
		keyDown = keyCodeDown;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		newGame();
		this.game = game;
		this.container = container;
		// music.loop();
		music.shuffle();
		music.play();
		// container.setMouseGrabbed(false);
		// all start up has finished so:
		// informing agent that game is ready to be played..
//		agent.gameStarted(this); // maybe nicer would be at newGame due to restarts once game is entered..
//		updateAIBoard();
	}

	/** Resets all values and starts a new game */
	public void newGame() {
		Log.info("New Game");
		resetInput();
		reset.reset();
		ballsToMove.clear();
		ballFactory.addTopBalls();
		container.setPaused(false);
		fireXSwingEvent(new XSwingEvent(this, GameEventType.GAME_SARTED));
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		music.pause();
	}

	@Override
	public void reset() {
		newGame();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		gui.render(g);
		ballsToMove.render(g);
		effectCatalog.render(g);
		scorePopups.render(g);
		pause.render(g);
		if (highScoreState != null && isGameOver) {
			highScoreState.render(container, game, g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		
		synchronized (aiCommunicator) {
			// first execute next drop command based on communicator value
			// if it is set
			int dropVal = aiCommunicator.getDropAt();
			if (dropVal != -1){
				dropBall(dropVal);
				aiCommunicator.updateBoard(ballTable.getBalls());
//				aiCommunicator.notify();
			}
			// if it is not set: agent has to calculate the next move, so we do nothing...
			// inform communicator that turn was done
			aiCommunicator.notify();
			
		}
		
		if (!isGameOver) {
			checkKeysMain(input);
			if (!container.isPaused()) { // no Input while game is paused
				checkKeysDuringGamee(input);
			}
		}

		if (!container.isPaused()) {
			gui.update(delta);
			effectCatalog.update(delta);
			scorePopups.update(delta);
			ballKiller.update(delta);
			if (mechanics.checkHight()) {
				gameOver(game);
			}
			ballKiller.update(delta);
			ballFactory.updateBalls(delta);
			if (ai != null) {
				ai.update(delta);
			}
		}
		if (highScoreState != null && isGameOver) {
			highScoreState.update(container, game, delta);
		}
	}

	/**
	 * Performs KeyEvents, which should be also performed during game is paused
	 * 
	 * @param input
	 *            GameInput
	 */
	private void checkKeysMain(Input input) {
		if (input.isKeyPressed(Input.KEY_P)) {
			// || (!container.isFullscreen() && !Mouse.isInsideWindow() &&
			// !container.isPaused())
			if (container.isPaused()) {
				fireXSwingEvent(new XSwingEvent(this, GameEventType.GAME_PAUSED));
			} else {
				fireXSwingEvent(new XSwingEvent(this, GameEventType.GAME_RESUMED));
			}
			container.setPaused(!container.isPaused());
			pause.setVisible(!pause.isVisible());
			resetInput();
		}
		if (input.isKeyPressed(Input.KEY_N)) {
			fireXSwingEvent(new XSwingEvent(this, GameEventType.GAME_STOPPED));
			newGame();
		}
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			fireXSwingEvent(new XSwingEvent(this, GameEventType.GAME_STOPPED));
			if (game.getCurrentState().getID() == XSwing.GAME_PANEL) {
				// Game is started with Menu
				Log.info("ESC pressed, swiching to main menu");
				SlickUtils.hideMouse(game.getContainer(), false);
				game.enterState(XSwing.START_SCREEN);
			} else {
				// Game is started without Menu
				Log.info("Exit Game wit ESC -no main menu");
				container.exit();
			}
		}

	}

	boolean controllerPressedLeft = false;
	boolean controllerPressedRight = false;
	boolean controllerPressedDown = false;

	/**
	 * Performs KeyEvents which should be not performed, during the game is paused
	 * 
	 * @param input
	 *            GameInput
	 */
	private void checkKeysDuringGamee(Input input) {

		if (input.isKeyPressed(keyLeft) || (!controllerPressedLeft && input.isControllerLeft(controllerID))) {
			notifyListener(new XSwingEvent(this, GameEventType.CANNON_MOVED_LEFT));
		}
		if (input.isKeyPressed(keyRight) || (!controllerPressedRight && input.isControllerRight(controllerID))) {
			notifyListener(new XSwingEvent(this, GameEventType.CANNON_MOVED_RIGHT));
		}
		if (input.isKeyPressed(keyDown) || (!controllerPressedDown && input.isControllerDown(controllerID))) {
			notifyListener(new XSwingEvent(this, GameEventType.PRESSED_DOWN));
		}
		if (input.isKeyPressed(Input.KEY_J)) {
			// ballFactory.addNewJoker();
			System.out.println(ballTable);
		}
		if (input.isKeyPressed(Input.KEY_K)) {
			// if (ballDropSimulator == null) {
			// ballDropSimulator = new BallDropSimulator();
			// }
			// ballDropSimulator.setBallTable(ballTable.clone());
		}
		if (input.isKeyPressed(Input.KEY_E)) {
			effectCatalog.setShowParticles(!effectCatalog.isShowParticles());
		}
		if (input.isKeyPressed(Input.KEY_B)) {
			ballFactory.toggleSpriteSheet();
		}
		if (input.isKeyPressed(Input.KEY_M)) {
			// ballFactory.addNewStone();
		}
		if (input.isKeyPressed(Input.KEY_F)) {
			container.setShowFPS(!container.isShowingFPS());
		}
		if (input.isKeyPressed(Input.KEY_S)) {
			// shrinkGame(game);
		}
		if (input.isKeyPressed(Input.KEY_H)) {
			highScore.setVisible(!highScore.isVisible());
		}
		controllerPressedLeft = input.isControllerLeft(controllerID);
		controllerPressedRight = input.isControllerRight(controllerID);
		controllerPressedDown = input.isControllerDown(controllerID);
		// if (input.isKeyPressed(Input.KEY_1)) {
		// canon.getBall().setNr(0);
		// }
		// if (input.isKeyPressed(Input.KEY_2)) {
		// canon.getBall().setNr(1);
		// }
		// if (input.isKeyPressed(Input.KEY_2)) {
		// canon.getBall().setNr(2);
		// }
		// if (input.isKeyPressed(Input.KEY_3)) {
		// canon.getBall().setNr(3);
		// }
		// if (input.isKeyPressed(Input.KEY_4)) {
		// canon.getBall().setNr(4);
		// }
		// if (input.isKeyPressed(Input.KEY_5)) {
		// canon.getBall().setNr(5);
		// }
		if (input.isKeyPressed(Input.KEY_F2)) {
			try {
				container.setFullscreen(!container.isFullscreen());
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean firstStart = true;

	/**
	 * Finishes the current game and switches to the highScoreTable
	 * 
	 * @param container
	 * @param game
	 * @throws SlickException
	 */
	private void gameOver(StateBasedGame game) throws SlickException {
		isGameOver = true;
		Log.info("Game Over");
		scoreScreenController.setHighScore(highScoreCounter.getScore());
		fireXSwingEvent(new XSwingEvent(this, GameEventType.GAME_OVER));
		if (firstStart) {
			highScoreState.fromXml("xswing/gui/high_score.xml", scoreScreenController);
			firstStart = false;
		}
		// highScoreState.init(container, game);
//		highScoreState.enter(container, game);
		// highScoreState.gotoScreenXSwing.GAME_OVER(; //VOID: ScreenID of NiftyGameState?)
		gameOver.play();
		container.setPaused(true);
		
		// here should be changed that game is played only once per start..
		synchronized (aiCommunicator) {
			aiCommunicator.setGameEnded(true);
			aiCommunicator.notify();
		}
		Log.info("AI scored: " + highScoreCounter.getScore());
		container.exit();
		
	}

	/**
	 * Clears all inputs. Useful to prevent keyEvents during pause mode or while entering the HighScore.
	 */
	private void resetInput() {
		container.getInput().clearControlPressedRecord();
		container.getInput().clearKeyPressedRecord();
		container.getInput().clearMousePressedRecord();
	}

	@Override
	public void gameEvent(XSwingEvent e) {
		if (e.getGameEventType() == GameEventType.CANNON_MOVED_LEFT) {
			canon.moveLeft();
		} else if (e.getGameEventType() == GameEventType.CANNON_MOVED_RIGHT) {
			canon.moveRight();
		}
		if (e.getGameEventType() == GameEventType.PRESSED_DOWN) {
			if (canon.isReadyToReleaseBall()) {
				Ball ball = ballFactory.getNewBall();
				notifyListener(new XSwingEvent(this, GameEventType.BALL_DROPPED, ball));
			}
		} else if (e.getGameEventType() == GameEventType.BALL_DROPPED) {
			canon.releaseBall(e.getBall());
		}
	}

	@Override
	public void ballEvent(BallEvent e) {
		if (e.getBallEventType() == BallEventType.BALL_HITS_GROUND || e.getBallEventType() == BallEventType.BALL_HITS_BALL) {
			mechanics.checkOfFive(e.getBall());
			mechanics.checkOfThree(e.getBall());
		} else if (e.getBallEventType() == BallEventType.BALL_EXPLODED) {
			scorePopups.add(new ScorePopup(fontScore, e.getBall().getX(), e.getBall().getY(), highScoreCounter.getBonus() + ""));
		} else if (e.getBallEventType() == BallEventType.BALL_WITH_THREE_IN_A_ROW) {
			// e.getBall().addBallEventListener(ballKiller);
		} else if (e.getBallEventType() == BallEventType.BALL_CAUGHT_BY_EXPLOSION) {
			effectCatalog.addEffect(e.getBall(), particleEffects.EXPLOSION);
			ballsToMove.remove(e.getBall());
			ballTable.remove(e.getBall());

		} else if (e.getBallEventType() == BallEventType.BALL_CAUGHT_BY_SHRINC) {
			effectCatalog.addEffect(e.getBall(), particleEffects.SHRINC);
		}
	}

	/**
	 * Adds an {@code BallEventListener} to the Ball.
	 * 
	 * @param listener
	 *            the {@code BallEventListener} to be added
	 */
	public void addXSwingListener(XSwingListener listener) {
		gameEventListeners.add(XSwingListener.class, listener);
	}

	/**
	 * Removes an {@code BallEventListener} from the Ball
	 * 
	 * @param listener
	 *            to be removed
	 */
	public void removeBallEventListener(XSwingListener listener) {
		gameEventListeners.remove(XSwingListener.class, listener);
	}

	/**
	 * Notifies all {@code XSwingListener}s about a {@code XSwingEvent}
	 * 
	 * @param event
	 *            the {@code XSwingEvent}
	 * @see EventListenerList
	 */
	protected void notifyListener(XSwingEvent event) {
		for (XSwingListener listener : gameEventListeners.getListeners(XSwingListener.class)) {
			listener.gameEvent(event);
		}
	}

	public void fireXSwingEvent(XSwingEvent event) {
		notifyListener(event);
	}

	public int getScore() {
		return highScoreCounter.getScore();
	}

	public void addJoker() {
		ballFactory.addNewJoker();
	}

	public void addStone() {
		ballFactory.addNewStone();
	}


//	private synchronized void updateAIBoard() {
//		aiBoard.updateBoard(ballTable.getBalls());
//		aiBoard.notify();
//	}

	@Override
	public void dropBall(int row) {
		if (row == canon.getCanonPosition()){
			Log.debug("Should be dropping... at row " + row);
			notifyListener(new XSwingEvent(this, GameEventType.PRESSED_DOWN));
//			if (canon.isReadyToReleaseBall()) {
//				Ball ball = ballFactory.getNewBall();
//				notifyListener(new XSwingEvent(this, GameEventType.BALL_DROPPED, ball));
//			}
		}
		else if (row < canon.getCanonPosition()){
			canon.moveLeft();
			dropBall(row);
//			try{
//				Thread.sleep(250);
//			}
//			catch (InterruptedException e){
//				Log.warn("Interrupted during dropBall waiting");
//			}
		}
		else{
			// row > canonPosition
			canon.moveRight();
			dropBall(row);
//			try{
//				Thread.sleep(250);
//			}
//			catch (InterruptedException e){
//				Log.warn("Interrupted during dropBall waiting");
//			}
		}
	}
	
	/**
	 * Adds an AI listener to the game. The listener should be an agent. He will be informed
	 * when the game is ready to be played.
	 * @param agent
	 */
	public void addAIListener(AIListener agent){
		this.agent = agent;
	}
	
	/**
	 * Adds an AI listener to the game. The listener should be an agent. He will be informed
	 * when the game is ready to be played.
	 * @param agent
	 */
	public void setAICommunicator(AICommunicator comm){
		this.aiCommunicator = comm;
	}

}