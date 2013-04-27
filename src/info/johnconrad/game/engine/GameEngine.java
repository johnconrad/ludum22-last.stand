package info.johnconrad.game.engine;

import info.johnconrad.game.engine.scenes.LogoScene;
import info.johnconrad.game.engine.scenes.Scene;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

@SuppressWarnings("serial")
public class GameEngine extends Applet {
	
	protected boolean isApplet = false;
	protected Thread gameThread = null;
	protected Canvas displayParent = null;
	
	protected static int width;
	protected static int height;
	
	protected static int fps;
	
	protected boolean done;
	protected boolean hasFocus = true;
	
	long lastFpsCheckTime = 0;
	long lastFrameTime = 0;
	long sinceLastFrame = 0;
	int frameCounter = 0;
	
	protected Queue<Scene> sceneQueue;
	protected Scene currScene = null;
	
	public GameEngine(int width, int height) {
		GameEngine.width = width;
		GameEngine.height = height;
	}
	
	public GameEngine() {
		this(640, 480);
	}

	protected void buildSceneQueue() {
		sceneQueue = new LinkedList<Scene>();
		sceneQueue.add(new LogoScene());
	}
	
	public void startGame() {
		if (gameThread != null) return;
		
		gameThread = new Thread() {
			public void run() {
				done = false;

				initDisplay();
				initGL();

				buildSceneQueue();
				
				gameLoop();
			}
		};
		
		gameThread.setName("Main Game Loop");
		gameThread.start();	
	}

	public void stopGame() {
		done = true;
		try {
			gameThread.join();
			gameThread = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void startApplet() {
		setLayout(new BorderLayout());
		try {
			displayParent = new Canvas() {
				public final void addNotify() {
					super.addNotify();
					startGame();
				}
				public final void removeNotify() {
					stopGame();
					super.removeNotify();
				}
			};
			displayParent.setSize(getWidth(),getHeight());
			add(displayParent);
			displayParent.setFocusable(true);
			displayParent.requestFocus();
			displayParent.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display");
		}
	}
	
	protected void initDisplay() {
		try {
			if (isApplet) initAppletDisplay();
			else initAppDisplay();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	protected void initAppDisplay() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));			
		Display.create();
	}
	
	protected void initAppletDisplay() throws LWJGLException {
		Display.setParent(displayParent);
		Display.create();
	}
	
	protected void initGL() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);				    // Black Background

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);							// Enable Smooth Shading
		GL11.glDisable(GL11.GL_LIGHTING);   

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                
		GL11.glClearDepth(1.0f);									// Depth Buffer Setup
		GL11.glEnable(GL11.GL_DEPTH_TEST);							// Enables Depth Testing
		GL11.glDepthFunc(GL11.GL_LEQUAL);							// The Type Of Depth Testing To Do
		
		//GL11.glEnable(GL11.GL_CULL_FACE);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);				

		// setup basic projection matrix
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		Project.gluPerspective(45.0f, ((float)width)/((float)height), 0.1f, 100.0f);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	protected void loadResources() {
		if (currScene != null) currScene.loadResources();
	}
	
	protected void freeResources() {
		if (currScene != null) currScene.freeResources();
	}
	
	protected void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		if (currScene != null) currScene.render(sinceLastFrame);
		
		Display.update();
	}
	
	protected void update() {
		if (currScene != null) currScene.update(sinceLastFrame);
	}
	
	private void processInput() {
		if (currScene != null) currScene.processInput();
	}
	
	protected void gameLoop() {
		currScene = sceneQueue.poll();  
		currScene.loadResources();
		
		while (currScene != null && !Display.isCloseRequested() && !done) {
			calcFPS();

			render();
			update();
			processInput();
			
			if (currScene.isDone()) {
				currScene.freeResources();
				
				currScene = currScene.getNextScene();
				if (currScene == null) currScene = sceneQueue.poll();
				
				if (currScene != null) { 
					currScene.loadResources();
					while (Keyboard.next()) Keyboard.getEventKey(); // empty keyboard buffer
				}
				
				
			}
			
			Display.sync(60);
		}
	}
	
	private void calcFPS() {
		long now = getTime();
		if (lastFpsCheckTime == 0) {
			lastFrameTime = now;
			lastFpsCheckTime = now;
			return;
		}
		
		sinceLastFrame = now - lastFrameTime;
		lastFrameTime = now;
		
		if (now - lastFpsCheckTime >= 1000) {
			fps = frameCounter;
			frameCounter = 0;
			lastFpsCheckTime += 1000;
		}
		
		frameCounter++;
	}
	
	public static long getTime() {
		return System.nanoTime() / 1000000;
	}
	
	public static int getRenderWidth() {
		return width;
	}

	public static int getRenderHeight() {
		return height;
	}

	public static int getFps() {
		return fps;
	}
	
	public void init() {
		isApplet = true;
		startApplet();
	}
	
	public static void main(String[] args) {
		GameEngine game = new GameEngine();
		game.startGame();
	}
}
