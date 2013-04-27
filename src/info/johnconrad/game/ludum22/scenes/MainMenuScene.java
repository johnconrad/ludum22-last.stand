package info.johnconrad.game.ludum22.scenes;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.AngelCodeFont;
import info.johnconrad.game.engine.graphics.Sprite;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.scenes.Scene;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class MainMenuScene extends Scene {
	private static final long FADE_IN_TIME = 3000;
	private static final long FADE_OUT_TIME = 500;
	
	boolean begun = false;
	boolean readyToFade = false;
	boolean startedFadeOut = false;
	
	long fadeOutStartTime = 0;
	
	Texture texture;
	Sprite background;
	AngelCodeFont font;
	public static Audio music;
	
	SpriteColor promptColor = new SpriteColor(0.246f, 0.695f, 1, 1);
	
	boolean done = false;
	long startTime;
	
	String continueText = "press ENTER to begin...";
	
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

		background.render(timeElapsed);
		
		float x = GameEngine.getRenderWidth() - font.getWidth(continueText) - 10;
		float y = font.getHeight(continueText) + 310;
		font.drawString(x, y, continueText, timeElapsed);

		
		endOrthoRender();
	}

	@Override
	public void update(long timeElapsed) {
		long now = GameEngine.getTime();
		
		// on first update, log start time and start fading in
		if (!begun) {
			startTime = GameEngine.getTime();
			background.fadeToColor(new SpriteColor(1f, 1f, 1f, 1f), FADE_IN_TIME);
			font.fadeToColor(new SpriteColor(promptColor.red, promptColor.green, promptColor.blue, 1), FADE_IN_TIME);
			begun = true;
			music.playAsMusic(1, 1, true);
		}
		
		// fade out now?
		if (!startedFadeOut && readyToFade) {
			background.fadeToColor(new SpriteColor(1f, 1f, 1f, 0f), FADE_OUT_TIME);
			font.fadeToColor(new SpriteColor(promptColor.red, promptColor.green, promptColor.blue, 0), FADE_OUT_TIME);
			startedFadeOut = true;
			fadeOutStartTime = now;
		}
			
		// at finish time, set flag indicating we are finished
		if (!done && startedFadeOut && now > fadeOutStartTime + FADE_OUT_TIME) {
			nextScene = new ExpositionScene();
			done = true;
		}
		
		// general entity updates
		background.update(timeElapsed);
		font.update(timeElapsed);
	}

	@Override
	public void processInput() {
		if (GameEngine.getTime() - startTime < 500) {
			while (Keyboard.next());
			return;
		}

		
		while (Keyboard.next()) {
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_RETURN:
				readyToFade = true;
			}
		}
	}

	@Override
	public void loadResources() {
		try {
			texture = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/mainmenu.png"));
			background = new Sprite(texture, 0.0f, 0.0f, 640f / 1024f, 480f / 1024f);
		    
		    background.setSize(new Vector2f(GameEngine.getRenderWidth(), GameEngine.getRenderHeight()));
		    background.setPosition(new Vector2f(0, 0));
		    background.setCentered(false);
		    background.setColor(new SpriteColor(1f, 1f, 1f, 0f));
		    
		    try {
				font = new AngelCodeFont("/font/inconsolata-15.fnt", "/font/inconsolata-15_0.png", false, 1f);
			    font.setColor(new SpriteColor(promptColor.red, promptColor.green, promptColor.blue, 0));
			} catch (SlickException e) {
				e.printStackTrace();
			}
			
			try {
				if (music == null) music = AudioLoader.getAudio("OGG", GameEngine.class.getResourceAsStream("/music/rich-boats.ogg"));
			} catch (IOException e) {
				System.out.println("Failed loading music!");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void freeResources() {
		background = null;
		texture = null;
	}
}
