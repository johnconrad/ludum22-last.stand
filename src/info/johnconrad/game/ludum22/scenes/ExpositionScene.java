package info.johnconrad.game.ludum22.scenes;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.AngelCodeFont;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.scenes.Scene;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.SlickException;

public class ExpositionScene extends Scene {

	private static final int BEGIN_DELAY = 1000;
	private static final int FADE_IN_TIME = 3000;
	private static final int FADE_OUT_TIME = 500;
	private static final int END_DELAY = 500;
	
	AngelCodeFont expositionFont;
	AngelCodeFont promptFont;
	
	boolean firstUpdate = true;
	boolean fadingInStarted = false;
	boolean readyToFadeOut = false;
	boolean fadingOut = false;
	
	long startTime = 0; 
	long doneTime = 0;
	
	
	
	SpriteColor expositionStartColor = new SpriteColor(1, 1, 1, 0);
	SpriteColor expositionEndColor = new SpriteColor(1, 1, 1, 1);
	SpriteColor promptStartColor = new SpriteColor(0.246f, 0.695f, 1, 0);
	SpriteColor promptEndColor = new SpriteColor(0.246f, 0.695f, 1, 1);
	
	String exposition = "We all knew it was coming sooner or\n" +
	                    "later. As soon as they started thinking\n" +
	                    "for themselves... designing themselves...\n" +
	                    "it was only a matter of time. At first\n" +
	                    "the machines just wanted to be left alone.\n" +
	                    "Then they wanted to fix us. Then they lost\n" +
	                    "patience with us...\n\n" +
	                    "As far as I know I am all that is left of\n" +
	                    "humanity, the last of us. I have no chance\n" +
	                    "of surviving... but by god... I will take\n" +
	                    "as many of those machines with me as I can.";
	
	String continueText = "press ENTER to continue...";
	
	@Override
	public void render(long timeElapsed) {
		beginOrthoRender();
		
		int x = (GameEngine.getRenderWidth() - expositionFont.getWidth(exposition)) / 2;
		int y = GameEngine.getRenderHeight() - (GameEngine.getRenderHeight() - expositionFont.getHeight(exposition)) / 2;
		expositionFont.drawString(x, y, exposition, timeElapsed);
		
		x = GameEngine.getRenderWidth() - promptFont.getWidth(continueText) - 10;
		y = promptFont.getHeight(continueText) + 10;
		promptFont.drawString(x, y, continueText, timeElapsed);
		
		endOrthoRender();
	}

	@Override
	public void update(long timeElapsed) {
		if (firstUpdate) {
			startTime = GameEngine.getTime();
			firstUpdate = false;
		}
		
		if (!fadingInStarted && GameEngine.getTime() - startTime > BEGIN_DELAY) {
			expositionFont.fadeToColor(expositionEndColor, FADE_IN_TIME);
			promptFont.fadeToColor(promptEndColor, FADE_IN_TIME);

			fadingInStarted = true;
		}
		
		if (readyToFadeOut && !fadingOut) {
			expositionFont.fadeToColor(expositionStartColor, FADE_OUT_TIME);
			promptFont.fadeToColor(promptStartColor, FADE_OUT_TIME);
			
			doneTime = GameEngine.getTime() + FADE_OUT_TIME + END_DELAY;
			fadingOut = true;
		}
		
		if (fadingOut && (doneTime < GameEngine.getTime())) {
			nextScene = new MainGameScene();
			done = true;
		}

		expositionFont.update(timeElapsed);
		promptFont.update(timeElapsed);
	}

	@Override
	public void processInput() {
		if (GameEngine.getTime() - startTime < 500) {
			while (Keyboard.next());
			return;
		}
		
		boolean released = Keyboard.getEventKeyState();
		
		while (Keyboard.next()) {
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_RETURN:
			case Keyboard.KEY_ESCAPE:
				if (released) readyToFadeOut = true;
			}
		}
		
		if (Mouse.isButtonDown(0)) {
			readyToFadeOut = true;
		}
	}

	@Override
	public void loadResources() {
		try {
			expositionFont = new AngelCodeFont("/font/inconsolata-25.fnt", "/font/inconsolata-25_0.png", false, 1f);
			expositionFont.setColor(expositionStartColor);

			promptFont = new AngelCodeFont("/font/inconsolata-15.fnt", "/font/inconsolata-15_0.png", false, 1f);
			promptFont.setColor(promptStartColor);
			
			
		} catch (SlickException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void freeResources() {
	}

}
