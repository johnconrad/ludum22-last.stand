package info.johnconrad.game.ludum22.map.bullets;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.entities.FreeMovingTopDownEntity;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;
import info.johnconrad.game.ludum22.map.entities.LivingEntity;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;

public class Bullet extends FreeMovingTopDownEntity {
	protected static Audio smallLaserSound = null;
	protected static Audio normalLaserSound = null;
	protected static Audio heavyLaserSound = null;
	
	private static boolean soundsLoaded = false;
	
	// cosmetic attributes
	protected SpriteColor color;
	protected float radius;
	protected Audio soundEffect;
	
	// gameplay affecting attributes
	float strength;
	int speed;
	int range;
	
	LivingEntity owner;
	Vector2f startingPos;
	
	public Bullet(TileMap map, Vector2f direction, float stength, int speed, int range) {
		super(map);
		loadSounds();
		
		this.strength = stength;
		this.speed = speed;
		this.range = range;
		this.useFriction = false;

		// defaults
		sizeInTiles = new Dimension(0, 0);
		color = new SpriteColor(0.247f, 0.694f, 1f, 1f);
		radius = 0.5f;
		soundEffect = normalLaserSound;

		// and away we go
		setVelocity((Vector2f)direction.normalise(null).scale(speed));		
	}
	
	private static void loadSounds() {
		if (soundsLoaded) return;
		
		soundsLoaded = true;
		
		try {
			smallLaserSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/sfx/laser-small.wav"));
			normalLaserSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/sfx/laser-normal.wav"));
			heavyLaserSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/sfx/laser-heavy.wav"));
		} catch (IOException e) {
			System.out.println("Failed loading attack sound effect!");
		}
	}
	
	public void fireSound() {
		if (soundEffect != null) soundEffect.playAsSoundEffect(1, 1, false);
	}
	
	public void collide(TileMapEntity otherObj) {
		super.collide(otherObj);
		
		if (otherObj == this.owner) return;
		
		if (otherObj instanceof LivingEntity) {
			if (((LivingEntity)otherObj).getFaction() == owner.getFaction()) return;
			
			((LivingEntity)otherObj).damage(this, strength);
			scheduledForRemoval = true;
		}
	}

	
	@Override
	public void render(long timeElapsed) {
		super.render(timeElapsed);

		renderParticalShot();
		//renderNormalBullet();
	}
	
	private void renderParticalShot() {
		float hardRadius = this.radius;
		
		Vector2f pos = getWorldPosition();
		
		GL11.glPointSize(2f);
		GL11.glBegin(GL11.GL_POINTS);
		Random rand = new Random(); 
		
		// main charge
		for (int i = 0; i < 50; i++) {
			float offsetX = rand.nextFloat() - 0.5f;
			float offsetY = rand.nextFloat() - 0.5f;
			float alpha = rand.nextFloat() * 0.5f + 0.5f;
			float finalRadius = rand.nextFloat() * hardRadius;
			GL11.glColor4f(color.red, color.green, color.blue, alpha);
			GL11.glVertex3f(pos.x + (offsetX * finalRadius) * map.getTileSize().x, 
					        pos.y + (offsetY * finalRadius) * map.getTileSize().y, 0);	//	+ offset * radius	
		}
		
		GL11.glEnd();
	}
	
	@SuppressWarnings("unused")
	private void renderNormalBullet() {
		Vector2f pos = getWorldPosition();
		
		GL11.glPointSize(6f);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glVertex3f(pos.x, pos.y, 0);
		GL11.glEnd();

	}

	@Override
	public void update(long timeElapsed) {
		super.update(timeElapsed);
		
		float distanceTraveled = Vector2f.sub(startingPos, position, null).length();
		if (distanceTraveled > range) 
			scheduledForRemoval = true;
		
		if (velocity.length() < 2)
			scheduledForRemoval = true;
	}
	
	public static Bullet getInstance(Class<? extends Bullet> c) {
		try {
			return c.getConstructor(TileMap.class, Vector2f.class).newInstance(null, new Vector2f());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public void setOwner(LivingEntity owner) {
		this.owner = owner;
	}

	public SpriteColor getColor() {
		return color;
	}

	public void setColor(SpriteColor color) {
		this.color = color;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		this.startingPos = new Vector2f(pos);
	}
	
	@Override
	protected void northCollision() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void southCollision() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void eastCollision() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void westCollision() {
		// TODO Auto-generated method stub

	}

}
