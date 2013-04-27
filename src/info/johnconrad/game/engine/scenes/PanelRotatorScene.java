package info.johnconrad.game.engine.scenes;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Sprite;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class PanelRotatorScene extends Scene {

	float xRot, yRot, zRot = 0;
	float xRotSpeed, yRotSpeed, zRotSpeed = 0;
	
	Texture avatar;
	Sprite avSprite;
	Audio grimMusic;
	
	@Override
	public void render(long timeElapsed) {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glColor4f(1f, 1f, 1f, 1f);
		
		xRot += xRotSpeed * (timeElapsed / 1000.0f);
		yRot += yRotSpeed * (timeElapsed / 1000.0f);
		zRot += zRotSpeed * (timeElapsed / 1000.0f);
		
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0f,0.0f,-6.0f);	
		GL11.glRotatef(xRot, 1.0f, 0.0f, 0.0f);		
		GL11.glRotatef(yRot, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
		
		avSprite.render(timeElapsed);
	}

	@Override
	public void update(long timeElapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processInput() {
		// detecting persistent key presses
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) 
			zRotSpeed -= 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) 
			zRotSpeed += 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) 
			yRotSpeed -= 1;;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) 
			yRotSpeed += 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) 
			xRotSpeed += 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_NEXT)) 
			xRotSpeed -= 1;

		// detecting down and up events
		while (Keyboard.next()) {
			System.out.println(String.format("%s was %s",
					           Keyboard.getKeyName(Keyboard.getEventKey()),
					           Keyboard.getEventKeyState() ? "pressed" : "released"));
			
			switch (Keyboard.getEventKey()) {
			case Keyboard.KEY_SPACE:
				xRotSpeed = 0;
				yRotSpeed = 0;
				zRotSpeed = 0;
				
				grimMusic.playAsMusic(1.0f, 1.0f, false);
				
				break;
			}
		}
		
		while (Mouse.next()) {
			if (!Mouse.getEventButtonState())
				continue;
			
			System.out.println(String.format("%s (%d) mouse button was clicked at %d,%d",
					           Mouse.getEventButton() == 0 ? "LEFT" : "RIGHT",
					           Mouse.getEventButton(),
					           Mouse.getEventX(), Mouse.getEventY()));
		}

	}

	@Override
	public void loadResources() {
		try {
			avatar = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/avatar.png"));
   	        avSprite = new Sprite(avatar, 0.5f, 0.5f, 1.0f, 1.0f);
   	        avSprite.setCentered(true);
		
		    grimMusic = AudioLoader.getAudio("OGG", GameEngine.class.getResourceAsStream("/music/grim.ogg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void freeResources() {
		if (grimMusic != null) grimMusic.stop();
		
		avatar = null;
		avSprite = null;
		grimMusic = null;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
