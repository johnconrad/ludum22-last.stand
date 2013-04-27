package info.johnconrad.game.ludum22.map.entities;

import info.johnconrad.game.engine.graphics.Animation;
import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.ludum22.map.GameMap.Faction;
import info.johnconrad.game.ludum22.map.bullets.PulseRifle;

import java.awt.Dimension;
import java.util.Arrays;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Hero extends LivingEntity {
	private static Texture texture = null;
	
	public Hero(TileMap map) {
		super(map);
		setAttributes();
		loadResources();
		
		health = maxHealth;
		shield = maxShield;
	}
	
	private void setAttributes() {
		faction = Faction.HUMANS;
		
		sizeInTiles = new Dimension(1,1);
		acceleration = 1.0f;
		walkingSpeed = 5.0f;
		runningSpeed = 7.0f;
		maxHealth = 20;
		maxShield = 99;
		shieldRechargeRate = 200;
		
		ammoType = PulseRifle.class;
		firingRate = 200;
	}

	
	@Override
	public void attack(Vector2f direction) {
		super.attack(direction);
		if (dead) return;
		
		fireBullet(direction);
	}
	
	@Override
	public void render(long timeElapsed) {
		super.render(timeElapsed);
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
		
		
		walkingAnimation = new Animation(texture, 0, 0, 40, 40, 4, 800);
		walkingAnimation.getFrameOrder().clear();
		walkingAnimation.getFrameOrder().addAll(Arrays.asList(new Integer[] {0, 1, 2, 3}));
		
		standingAnimation = new Animation(texture, 0, 0, 40, 40, 1, 1000);
		
		attackAnimation = new Animation(texture, 0, 41, 40, 40, 4, 400);
		attackAnimation.getFrameOrder().clear();
		attackAnimation.getFrameOrder().addAll(Arrays.asList(new Integer[] {0, 1, 2, 3}));
		
		deathAnimation = new Animation(texture, 0, 0, 40, 40, 1, 1000);
		
		/*
		try {
			attackSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/music/shot.wav"));
		} catch (IOException e) {
			System.out.println("Failed loading gun shot sound effect!");
		}
		*/

	}

	public static void setTexture(Texture texture) {
		Hero.texture = texture;
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
