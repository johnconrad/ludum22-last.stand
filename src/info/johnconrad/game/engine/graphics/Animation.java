package info.johnconrad.game.engine.graphics;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Animation extends Renderable2D {
	ArrayList<Integer> frameOrder;
	ArrayList<Sprite> sprites;
	
	SpriteColor color = null;
	
	protected int animationDefaultLength;
	
	protected long animationStartTime;
	protected int animationLength;
	protected boolean inMotion = false;
	protected boolean stopping = false;

	public ArrayList<Integer> getFrameOrder() {
		return frameOrder;
	}

	public void setFrameOrder(ArrayList<Integer> frameOrder) {
		this.frameOrder = frameOrder;
	}

	public Animation(Texture texture, int x, int y, int pixelWidth, int pixelHeight, int frames, int length) {
		size = new Vector2f(pixelWidth, pixelHeight);
		position = new Vector2f(0f,0f);
		
		int imageWidth = texture.getImageWidth();
		int imageHeight = texture.getImageHeight();

		sprites = new ArrayList<Sprite>();
		frameOrder = new ArrayList<Integer>();
		
		animationLength = length;
		animationDefaultLength = length;
		
		for(int i = 0; i < frames; i++) {
			float offset = (pixelWidth + 1) * i;
			float s = (x + offset + 1) / (float) imageWidth;
			float t = (y + 1) / (float) imageHeight;
			float r = (x + pixelWidth + offset) / (float) imageWidth;
			float q = (y + pixelHeight) / (float) imageHeight;
			
			Sprite newSprite = new Sprite(texture, s, t, r, q);
			newSprite.setSize(new Vector2f(size));
			newSprite.setCentered(false);
			
			// for debugging
			//newSprite.setDrawFrame(true);
			
			sprites.add(newSprite);
			frameOrder.add(i);
		}
	}
	
	public void startAnimation() {
		startAnimation(true, animationDefaultLength);
	}
	
	public void startAnimation(boolean loop) {
		startAnimation(loop, animationDefaultLength);
	}
	
	public void startAnimation(boolean loop, int length) {
		animationStartTime = GameEngine.getTime();
		animationLength = length;
		inMotion = true;
		stopping = !loop;
	}
	
	public void stopAnimation(boolean complete) {
		stopping = true;
		inMotion = complete;
	}
	
	@Override
	public void render(long timeElapsed) {
		int frame;
		
		if (!inMotion || (stopping && GameEngine.getTime() - animationStartTime > animationLength)) {
			frame = frameOrder.size() - 1;
			inMotion = false;
		} else {
			frame = (int) (((GameEngine.getTime() - animationStartTime) % animationLength) / ((float)animationLength / frameOrder.size()));
		}
		if (frame >= frameOrder.size()) {
			System.out.println("oops!");
			return;
		}

		Sprite currFrame = sprites.get(frameOrder.get(frame));
		currFrame.position = position;
		currFrame.mirrored = mirrored;
		currFrame.setSize(getSize());
		currFrame.setColorOverride(color);
		
		currFrame.render(timeElapsed);
	}

	@Override
	public void update(long timeElapsed) {
		for(Sprite currSprite: sprites) {
			currSprite.update(timeElapsed);
		}
	}

	public boolean isInMotion() {
		return inMotion;
	}

	public boolean isStopping() {
		return stopping;
	}

	public SpriteColor getColor() {
		return color;
	}

	public void setColor(SpriteColor color) {
		this.color = color;
	}
}
