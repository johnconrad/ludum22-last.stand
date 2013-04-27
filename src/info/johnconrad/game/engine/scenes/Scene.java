package info.johnconrad.game.engine.scenes;

import info.johnconrad.game.engine.GameEngine;

import org.lwjgl.opengl.GL11;

public abstract class Scene {
	protected boolean done = false;
	
	protected GameEngine engine;
	protected Scene nextScene = null;
	
	public void setGameEngine(GameEngine engine) {
		this.engine = engine;
	}
	
	public Scene getNextScene() {
		return nextScene;
	}

	public void stop() {
		done = true;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public abstract void render(long timeElapsed);
	public abstract void update(long timeElapsed);
	public abstract void processInput();
	
	public abstract void loadResources();
	public abstract void freeResources();
	
	protected void beginOrthoRender() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, GameEngine.getRenderWidth(), 0, GameEngine.getRenderHeight(), -1, 1);
		
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
	}
	
	protected void endOrthoRender() {
		GL11.glPopMatrix();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
	}
	
}
