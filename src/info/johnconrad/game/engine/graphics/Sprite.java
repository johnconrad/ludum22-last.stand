package info.johnconrad.game.engine.graphics;

import info.johnconrad.game.engine.GameEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Sprite extends Renderable2D {
	public static class SpriteColor {
		public float red = 1f;
		public float green = 1f;
		public float blue = 1f;
		public float alpha = 1f;
		
		public SpriteColor() {
		}

		public SpriteColor(SpriteColor color) {
			if (color != null) {
				red = color.red;
				green = color.green;
				blue = color.blue;
				alpha = color.alpha;
			}
		}

		
		public SpriteColor(float red, float green, float blue, float alpha) {
			this.red = red;
			this.blue = blue;
			this.green = green;
			this.alpha = alpha;
		}

	}
	
	// standard current state of the sprite variables
	protected SpriteColor color;
	protected SpriteColor colorOverride = null;
	
	protected Texture  texture;
	protected float    s;
	protected float    t;
	protected float    r;
	protected float    q;
	
	protected boolean  centered;
	
	// for fading functionality
	boolean fading = false;
	long startFadingTime;
	long fadingDuration;
	protected SpriteColor originalColor;
	protected SpriteColor finalColor;

	boolean drawFrame = false;
	
	public Sprite(Texture texture) {
		init(texture, 0f, 0f, 1f, 1f);
		size = new Vector2f(texture.getImageWidth(), texture.getImageHeight());
	}

	public Sprite(Texture texture, int x, int y, int width, int height) {
		int imageWidth = texture.getImageWidth();
		int imageHeight = texture.getImageHeight();
		
		float s = (x + 1) / (float) imageWidth;
		float t = (y + 1) / (float) imageHeight;
		float r = (x + width) / (float) imageWidth;
		float q = (y + height) / (float) imageHeight;
		
		size = new Vector2f(width, height);
		
		init(texture, s, t, r, q);
	}
	
	public Sprite(Texture texture, float s, float t, float r, float q) {
		init(texture, s, t, r, q);
		size = new Vector2f(2, 2);
	}
	
	private void init(Texture texture, float s, float t, float r, float q) {
		this.texture = texture;
		this.s = s;
		this.t = t;
		this.r = r;
		this.q = q;
		
		position = new Vector2f(0, 0);
		color = new SpriteColor();
		
		centered = false;
	}
	
	public void fadeToColor(SpriteColor finalColor, long duration) {
		fading = true;
		startFadingTime = GameEngine.getTime();
		fadingDuration = duration;
		originalColor = new SpriteColor(color);
		this.finalColor = finalColor;
	}

	public void render(long timeElapsed) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		
		float xOffset = 0;
		float yOffset = 0;
		
		if (centered) {
			xOffset = size.x / 2.0f;
			yOffset = size.y / 2.0f;
		}
		
		float sa = mirrored ? r : s;
		float ra = mirrored ? s : r;
		float ta = flipped ? q : t;
		float qa = flipped ? t : q;
		
		SpriteColor usedColor = colorOverride == null ? color : colorOverride;
		
		float fudge = 0.002f;
		
		//System.out.println(color.red + ", " + color.green + ", " + color.blue + ", " + color.alpha);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(usedColor.red, usedColor.green, usedColor.blue, usedColor.alpha);
		GL11.glTexCoord2f(sa+fudge, qa-fudge);
		GL11.glVertex2f(position.x - xOffset, position.y - yOffset);
		GL11.glTexCoord2f(ra-fudge, qa-fudge);
		GL11.glVertex2f(position.x + size.x - xOffset, position.y - yOffset);
		GL11.glTexCoord2f(ra-fudge, ta+fudge);
		GL11.glVertex2f(position.x + size.x - xOffset, position.y + size.y - yOffset);
		GL11.glTexCoord2f(sa+fudge, ta+fudge);
		GL11.glVertex2f(position.x - xOffset, position.y + size.y - yOffset);
		GL11.glEnd();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		if (drawFrame) {
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glColor4f(1f, 0f, 0f, 1f);
			GL11.glVertex2f(position.x - xOffset, position.y - yOffset);
			GL11.glVertex2f(position.x + size.x - xOffset, position.y - yOffset);
			GL11.glVertex2f(position.x + size.x - xOffset, position.y + size.y - yOffset);
			GL11.glVertex2f(position.x - xOffset, position.y + size.y - yOffset);
			GL11.glEnd();
		}
	}
	
	@Override
	public void update(long timeElapsed) {
		// fading logic
		if (fading) {
			long now = GameEngine.getTime();
			
			// if we are done, set final color and quit
			if (now > startFadingTime + fadingDuration) {
				color = finalColor;
				fading = false;
			}
			
			// otherwise set the scaled fading value
			else {
				float progress = (float) (now - startFadingTime) / (float) fadingDuration;
				
				color.red = ((finalColor.red - originalColor.red) * progress) + originalColor.red;
				color.green = ((finalColor.green - originalColor.green) * progress) + originalColor.green;
				color.blue = ((finalColor.blue - originalColor.blue) * progress) + originalColor.blue;
				color.alpha = ((finalColor.alpha - originalColor.alpha) * progress) + originalColor.alpha;
				
				//System.out.println(progress + ": " + originalColor.alpha + " -> " + finalColor.alpha + " (" + color.alpha + ")");
			}
		}
	}

	public boolean isDrawFrame() {
		return drawFrame;
	}

	public void setDrawFrame(boolean drawFrame) {
		this.drawFrame = drawFrame;
	}
	
	
	public boolean isCentered() {
		return centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	public SpriteColor getColor() {
		return color;
	}

	public void setColor(SpriteColor color) {
		this.color = new SpriteColor(color);
	}

	public SpriteColor getColorOverride() {
		return colorOverride;
	}

	public void setColorOverride(SpriteColor colorOverride) {
		this.colorOverride = new SpriteColor(colorOverride);
	}

	public boolean isFading() {
		return fading;
	}
}
