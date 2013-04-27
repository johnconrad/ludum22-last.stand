package info.johnconrad.game.ludum22.map.entities;

import info.johnconrad.game.engine.graphics.Animation;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.ludum22.map.GameMap.Faction;
import info.johnconrad.game.ludum22.map.bullets.PowerShot;
import info.johnconrad.game.ludum22.map.logic.AssaultLogic;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class SpiderGuard extends LivingEntity {
	private static Texture texture = null;
	protected SpriteColor coreColor = new SpriteColor(0.247f, 0.694f, 1f, 1f);

	public SpiderGuard(TileMap map) {
		super(map);
		setAttributes();
		loadResources();
		
		health = maxHealth;
		logic = new AssaultLogic(map, this);
	}
	
	private void setAttributes() {
		faction = Faction.MACHINES;
		
		sizeInTiles = new Dimension(2,2);
		acceleration = 1.0f;
		walkingSpeed = 5.0f;
		runningSpeed = 7.0f;
		
		maxHealth = 1;
		maxShield = 80;
		
		ammoType = PowerShot.class;
		firingRate = 1000;
		
		coreColor = new SpriteColor(1f, 0.694f, 0.247f, 1f);
	}

	
	@Override
	public void attack(Vector2f direction) {
		super.attack(direction);
		if (dead) return;
		
		fireBullet(direction);
	}
	
	@Override
	public void render(long timeElapsed) {
		renderCore();
		super.render(timeElapsed);
	}
	
	private void renderCore() {
		float hardRadius = 0.6f;
		
		Vector2f pos = getWorldPosition();
		pos.x += getSizeInTiles().width * map.getTileSize().x * 0.5f;
		pos.y += getSizeInTiles().height * map.getTileSize().y * 0.5f;
		
		GL11.glPointSize(2f);
		GL11.glBegin(GL11.GL_POINTS);
		Random rand = new Random(); 
		
		// main charge
		for (int i = 0; i < 50; i++) {
			float offsetX = rand.nextFloat() - 0.5f;
			float offsetY = rand.nextFloat() - 0.5f;
			float alpha = rand.nextFloat() * 0.5f + 0.5f;
			float radius = rand.nextFloat() * hardRadius;
			GL11.glColor4f(coreColor.red, coreColor.green, coreColor.blue, alpha);
			GL11.glVertex3f(pos.x + (offsetX * radius) * map.getTileSize().x, 
					        pos.y + (offsetY * radius) * map.getTileSize().y, 0);		
		}
		
		GL11.glEnd();
	}
	
	@Override
	public void update(long timeElapsed) {
		super.update(timeElapsed);
		
		// for live debuging. REMOVE LATER!
		setAttributes();
	}

	@Override
	protected void loadResources() {
		if (texture == null) {
			System.out.println("Missing texture for the hero!");
			return;
		}
		
		
		walkingAnimation = new Animation(texture, 0, 82, 80, 80, 3, 500);
		walkingAnimation.getFrameOrder().clear();
		walkingAnimation.getFrameOrder().addAll(Arrays.asList(new Integer[] {0, 1, 0, 2}));
		
		standingAnimation = new Animation(texture, 81, 82, 80, 80, 1, 1000);
		
		attackAnimation = new Animation(texture, 0, 82, 80, 80, 3, 500);
		attackAnimation.getFrameOrder().clear();
		attackAnimation.getFrameOrder().addAll(Arrays.asList(new Integer[] {0, 1, 0, 2}));
		
		deathAnimation = new Animation(texture, 81, 82, 80, 80, 1, 1000);
		
		/*
		try {
			attackSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/music/shot.wav"));
		} catch (IOException e) {
			System.out.println("Failed loading gun shot sound effect!");
		}
		*/

	}

	public static void setTexture(Texture texture) {
		SpiderGuard.texture = texture;
	}

	@Override
	protected void northCollision() {}

	@Override
	protected void southCollision() {}

	@Override
	protected void eastCollision() {}

	@Override
	protected void westCollision() {}

}
