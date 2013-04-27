package info.johnconrad.game.engine.graphics;

import org.lwjgl.util.vector.Vector2f;

public abstract class Renderable2D {
	protected Vector2f position = new Vector2f(0, 0);
	protected Vector2f size;
	protected boolean mirrored = false;
	protected boolean flipped = false;

	public abstract void render(long timeElapsed);
	public abstract void update(long timeElapsed);
	
	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public Vector2f getSize() {
		return size;
	}
	
	public void setSize(Vector2f size) {
		this.size = size;
	}
	
	public boolean isMirrored() {
		return mirrored;
	}

	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}
	
	public boolean isFlipped() {
		return flipped;
	}
	
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
}
