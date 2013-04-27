package info.johnconrad.game.ludum22;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.scenes.Scene;
import info.johnconrad.game.ludum22.scenes.MainMenuScene;

import java.util.LinkedList;

import org.lwjgl.opengl.Display;

@SuppressWarnings("serial")
public class Ludum22Game extends GameEngine {
	public Ludum22Game() {
		super(800, 600);
		
		Display.setTitle("Last Stand");
	}
	
	protected void buildSceneQueue() {
		sceneQueue = new LinkedList<Scene>();
		//sceneQueue.add(new LogoScene());
		sceneQueue.add(new MainMenuScene());		
	}
	
	public static void main(String[] args) {
		Ludum22Game game = new Ludum22Game();
		game.startGame();
	}
}
