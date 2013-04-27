package info.johnconrad.game.ludum22.scenes;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.scenes.Scene;
import info.johnconrad.game.ludum22.map.GameMap;
import info.johnconrad.game.ludum22.widgets.CounterWidget;
import info.johnconrad.game.ludum22.widgets.GameOverOverlay;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class MainGameScene extends Scene {

	Texture textures;
	
	GameMap map;
	CounterWidget shields;
	CounterWidget health;
	CounterWidget kills;
	GameOverOverlay exterminated;
	
	boolean gameover = false;
	
	@Override
	public void render(long timeElapsed) {
		beginOrthoRender();
		
		map.render(gameover ? 0 : timeElapsed);
		shields.render(gameover ? 0 : timeElapsed);
		health.render(gameover ? 0 : timeElapsed);
		kills.render(gameover ? 0 : timeElapsed);
		
		if (gameover) {
			exterminated.render(timeElapsed);
		}
		
		endOrthoRender();
	}
	
	@Override
	public void update(long timeElapsed) {
		gameover = map.getPlayer().isDead();
		
		map.update(gameover ? 0 : timeElapsed);
		shields.update(timeElapsed);
		shields.setCount(Math.round(map.getPlayer().getShield()));
		
		health.update(timeElapsed);
		health.setCount(Math.round(map.getPlayer().getHealth()));
		
		kills.update(timeElapsed);
		kills.setCount(map.getPlayer().getKills());
		
		if (gameover) {
			exterminated.update(timeElapsed);
		}

	}

	@Override
	public void processInput() {
		
		while (Mouse.next()) {
			boolean clicked = Mouse.getEventButtonState();
			int button = Mouse.getEventButton();
			if (button == -1) continue; // movement, not button click
			System.out.println(String.format("Button #%d was %s.", button, clicked ? "clicked" : "released"));
		}
		
		if (Mouse.isButtonDown(0)) {
			Vector2f clickLocation = new Vector2f(Mouse.getX(), Mouse.getY());
			Vector2f offsetByPlayerLoc = Vector2f.sub(clickLocation, map.getPlayer().getWorldPosition(), null);
			Vector2f offsetBySize = new Vector2f(offsetByPlayerLoc);
			offsetBySize.x -= map.getPlayer().getSizeInTiles().width * map.getTileSize().x * 0.5f;
			offsetBySize.y -= map.getPlayer().getSizeInTiles().height * map.getTileSize().y * 0.5f;
			
			map.getPlayer().attack(offsetBySize);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			map.getPlayer().move(new Vector2f(0, 1));
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			map.getPlayer().move(new Vector2f(-1, 0));
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			map.getPlayer().move(new Vector2f(0, -1));
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			map.getPlayer().move(new Vector2f(1, 0));
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			map.getPlayer().setRunning(true);
		} else {
			map.getPlayer().setRunning(false);
		}

		
		boolean released = Keyboard.getEventKeyState();
		while (Keyboard.next() && released) {
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_RETURN:
				if (gameover) {
					nextScene = new MainMenuScene();
					done = true;
				}
				shields.setCount(shields.getCount()+1);
				break;
			case Keyboard.KEY_F11:
				map.setDrawBoundingBoxes(!map.isDrawBoundingBoxes());
				break;
			case Keyboard.KEY_F12:
				nextScene = new MainGameScene();
				done = true;
				break;
			}
		}
	}

	@Override
	public void loadResources() {
		try {
			textures = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/sprites.png"));
			
			shields = new CounterWidget(textures, 0, 0);
			shields.setPosition(new Vector2f(GameEngine.getRenderWidth() - shields.getSize().x, 
					                         GameEngine.getRenderHeight() - shields.getSize().y));
			
			health = new CounterWidget(textures, 82, 0);
			health.setPosition(new Vector2f(GameEngine.getRenderWidth() - shields.getSize().x - health.getSize().x, 
                                            GameEngine.getRenderHeight() - health.getSize().y));


			kills = new CounterWidget(textures, 0, 21);
			kills.setPosition(new Vector2f(0, GameEngine.getRenderHeight() - kills.getSize().y));

			
			map = new GameMap();
			map.setSize(new Vector2f(GameEngine.getRenderWidth(), GameEngine.getRenderHeight()));
			map.calcViewSize();
			
			exterminated = new GameOverOverlay();
		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void freeResources() {
	}

}
