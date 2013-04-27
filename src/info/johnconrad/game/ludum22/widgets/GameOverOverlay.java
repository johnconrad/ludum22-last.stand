package info.johnconrad.game.ludum22.widgets;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.AngelCodeFont;
import info.johnconrad.game.engine.graphics.Renderable2D;
import info.johnconrad.game.engine.graphics.Sprite;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.ludum22.scenes.MainMenuScene;

import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class GameOverOverlay extends Renderable2D {
	static Audio losingSound; 
	Texture texture;
	Sprite sprite;
	AngelCodeFont font;
	
	SpriteColor promptColor = new SpriteColor(0.246f, 0.695f, 1, 1);
	
	String returnToMenuText = "press ENTER to abandon all hope...";
	
	public GameOverOverlay() {
		try {
			texture = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/gameover.png"));
			
			sprite = new Sprite(texture, 0.0f, 0.0f, 800f / 1024f, 600f / 1024f);
			sprite.setSize(new Vector2f(GameEngine.getRenderWidth(), GameEngine.getRenderHeight()));
			sprite.setPosition(new Vector2f(0, 0));
			sprite.setCentered(false);
			sprite.setColor(new SpriteColor(1f, 1f, 1f, 1f));
			
		    try {
				font = new AngelCodeFont("/font/inconsolata-15.fnt", "/font/inconsolata-15_0.png", false, 1f);
			    font.setColor(new SpriteColor(promptColor.red, promptColor.green, promptColor.blue, 1));
			} catch (SlickException e) {
				e.printStackTrace();
			}
			
			try {
				if (losingSound == null) losingSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/sfx/gameover.wav"));
			} catch (IOException e) {
				System.out.println("Failed loading music!");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(long timeElapsed) {
		if (MainMenuScene.music.isPlaying()) { 
			MainMenuScene.music.stop();
			losingSound.playAsSoundEffect(1, 1, false);
		}
		
		sprite.render(timeElapsed);
		
		float x = GameEngine.getRenderWidth() - font.getWidth(returnToMenuText) - 10;
		float y = font.getHeight(returnToMenuText) + 220;
		font.drawString(x, y, returnToMenuText, timeElapsed);
	}

	@Override
	public void update(long timeElapsed) {
		sprite.update(timeElapsed);
		font.update(timeElapsed);
	}

}
