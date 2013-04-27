package info.johnconrad.game.engine.tilemap.entities;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Animation;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;

import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;


public abstract class WalkingSideScrollerEntity extends FreeMovingSideScrollerEntity {
	public static enum Direction {
		LEFT, RIGHT
	}
	
	public static enum Aim { UP, STRAIT, DOWN };
	
	// resources used to present the entity
	protected Animation walkingAnimation = null;
	protected Animation standingAnimation = null;
	protected Animation attackAnimation = null;
	protected Animation deathAnimation = null;
	
	protected Audio attackSound = null;
	protected Audio finishJumpSound = null;
	protected Audio deathSound = null;
	
	// flashing animation state variables
	protected boolean flashing = false;
	protected long flashStartTime;
	protected long flashLength;
	protected long flashPulses;
	protected float flashR;
	protected float flashG;
	protected float flashB;
	protected float flashA;
	
	// general traits of the entity
	protected float walkingSpeed = 5f;
	protected float jumpStrength = 15f;
	protected int maxHealth = 9;
	protected float fallDistanceForDamage = 25f;
	protected long damageGracePeriod = 100;

	// current state of the entity
	protected Direction direction = Direction.RIGHT;
	protected Aim aim = Aim.STRAIT;
	protected boolean dead;
	protected int health = maxHealth;
	protected long lastDamageTime = 0;
	
	protected Vector2f lastStableFooting = null;

	public WalkingSideScrollerEntity(TileMap map) {
		super(map);
	}
	
	public void jump() {
		jump(jumpStrength);
	}
	
	public void jump(float speed) {
		if (dead) return;
		
		if (velocity.y == 0f) {
			velocity.y += speed;
		}
	}
	
	public void attack() {
		if (dead) return;
		
		attackAnimation.startAnimation(false);
		if (attackSound != null)
			attackSound.playAsSoundEffect(1, 1, false);
	}
	
	public Aim getAim() {
		return aim;
	}

	public void setAim(Aim aim) {
		this.aim = aim;
	}

	public void walk(Direction dir) {
		walk(walkingSpeed * (dir == Direction.RIGHT ? 1 : -1));
	}
	
	public void walk(float speed) {
		if (dead) return;
		
		velocity.x = speed;
		
		if (speed == 0) {
			walkingAnimation.stopAnimation(false);
		}
		
		if (speed != 0 && !walkingAnimation.isInMotion()) {
			if (speed > 0) direction = Direction.RIGHT;
			else direction = Direction.LEFT;
			walkingAnimation.startAnimation();
		}
	}

	public void damage(int amount) {
		long now = GameEngine.getTime();
	 	if (amount == 0 || now - lastDamageTime < damageGracePeriod) return;
		
	 	lastDamageTime = now;
		health -= amount;
		if (health < 0) health = 0;
		
		if (health == 0)
			die();
		else 
		 	flash(1.0f, 0.7f, 0.7f, 1, damageGracePeriod, 2);
		
	}
	
	public void die() {
		if (dead) return;
		
		dead = true;
		
		if (deathAnimation != null) deathAnimation.startAnimation(false);
		if (deathSound != null) deathSound.playAsSoundEffect(1, 1, false);
		
	}
	
	protected void floorCollision() {
		if (lastStableFooting == null) lastStableFooting = new Vector2f(position);
		
		// calculate fall damage
		float fallDistance = lastStableFooting.y - position.y;
		int dmgAmount = (int) Math.floor(fallDistance / fallDistanceForDamage);
		if (dmgAmount < 0) dmgAmount = 0;
		damage(dmgAmount);

		lastStableFooting = new Vector2f(position);
		
		if (finishJumpSound != null) {
			finishJumpSound.playAsSoundEffect(1, 1, false);
		}
	}
	
	public void update(long timeElapsed) {
		super.update(timeElapsed);
		
		if (health > maxHealth) health = maxHealth;
		if (lastStableFooting == null) lastStableFooting = new Vector2f(position);
	}
	
	public void render(long timeElapsed) {
		Vector2f pos = getWorldPosition();

		Animation activeAnimation = standingAnimation;
		
		if (dead && deathAnimation != null) 
			activeAnimation = deathAnimation;
		else if (!dead && attackAnimation != null && attackAnimation.isInMotion()) 
			activeAnimation = attackAnimation;
		else if (!dead && walkingAnimation != null && walkingAnimation.isInMotion()) 
			activeAnimation = walkingAnimation;

		if (activeAnimation == null) return;
		
		float scale = calcFlashScale();
		if(flashing) {
			SpriteColor color = new SpriteColor(scaleColor(flashR, scale), scaleColor(flashG, scale), scaleColor(flashB, scale) ,scaleColor(flashA, scale));
			activeAnimation.setColor(color); 
		} else {
			activeAnimation.setColor(null);
		}
		
		activeAnimation.setMirrored(direction == Direction.RIGHT);
		activeAnimation.setPosition(pos);
		activeAnimation.render(timeElapsed);			

		super.render(timeElapsed);
	}
	
	public void reset() {
		super.reset();
		
		dead = false;
		health = maxHealth;
		lastStableFooting = null;
	}
	
	public void flash(float r, float g, float b, float a, long length, int pulses) {
		flashing = true;
		flashStartTime = GameEngine.getTime();
		flashPulses = pulses;
		flashLength = length;
		flashR = r;
		flashG = g;
		flashB = b;
		flashA = a;
		
	}

	protected float scaleColor(float color, float scale) {
		return 1f - (1f - color) * scale;
	}
	
	protected float calcFlashScale() {
		if (!flashing)
			return 1.0f;
		
		long now = GameEngine.getTime();
		long duration = now - flashStartTime; 
		if (duration > flashLength) {
			flashing = false;
			return 1.0f;
		}
		
		float pulseLength = flashLength / flashPulses;
		float pulsePos = (duration % pulseLength) / pulseLength;
		float scale = (float) Math.sin(pulsePos * Math.PI);
		
		return scale;
	}
	
	public void loadResources() {
		try {
			finishJumpSound = AudioLoader.getAudio("WAV", GameEngine.class.getResourceAsStream("/music/land.wav"));
		} catch (IOException e) {
			System.out.println("Failed loading landing sound effect!");
		}
	}
}
