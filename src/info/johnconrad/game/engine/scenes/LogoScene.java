package info.johnconrad.game.engine.scenes;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Sprite;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class LogoScene extends Scene {

	private static final long TOTAL_TIME = 12000;
	private static final long FADE_IN_TIME = 4000;
	private static final long FADE_OUT_TIME = 500;
	
	boolean begun = false;
	boolean startedFadeOut = false;
	
	Texture logo;
	Sprite logoSprite;
	Audio tribalMusic;
	
	boolean done = false;
	long startTime;
		
	@Override
	public void stop() {
		done = true;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	
	@Override
	public void render(long timeElapsed) {
		beginOrthoRender();

		logoSprite.render(timeElapsed);
		
		endOrthoRender();
	}

	@Override
	public void update(long timeElapsed) {
		long now = GameEngine.getTime();
		
		// on first update, log start time and start fading in
		if (!begun) {
			startTime = GameEngine.getTime();
			logoSprite.fadeToColor(new SpriteColor(1f, 1f, 1f, 1f), FADE_IN_TIME);
			tribalMusic.playAsMusic(1f, 1f, false);
			begun = true;
		}
		
		// at fade out time kick off fade out
		if (!startedFadeOut && now > startTime + TOTAL_TIME - FADE_OUT_TIME) {
			logoSprite.fadeToColor(new SpriteColor(1f, 1f, 1f, 0f), FADE_OUT_TIME);
			startedFadeOut = true;
		}
			
		// at finish time, set flag indicating we are finished
		if (!done && now > startTime + TOTAL_TIME) {
			done = true;
		}
		
		// general entity updates
		logoSprite.update(timeElapsed);
	}

	@Override
	public void processInput() {
		while (Keyboard.next()) {
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_ESCAPE:
				done = true;
			}
		}
	}

	@Override
	public void loadResources() {
		try {
			logo = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/inigo-who.png"));
			logoSprite = new Sprite(logo, 0.0f, 0.0f, 640f / 1024f, 480f / 1024f);
		    
		    logoSprite.setSize(new Vector2f(GameEngine.getRenderWidth(), GameEngine.getRenderHeight()));
		    logoSprite.setPosition(new Vector2f(0, 0));
		    logoSprite.setCentered(false);
		    logoSprite.setColor(new SpriteColor(1f, 1f, 1f, 0f));
			
		    tribalMusic = AudioLoader.getAudio("OGG", GameEngine.class.getResourceAsStream("/music/tribal-short.ogg"));
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void freeResources() {
		if (tribalMusic != null) tribalMusic.stop();
		
		tribalMusic = null;
		logoSprite = null;
		logo = null;
		
		
	}


}
