package info.johnconrad.game.ludum22.map.entities;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Animation;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.entities.FreeMovingTopDownEntity;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;
import info.johnconrad.game.ludum22.map.GameMap.Faction;
import info.johnconrad.game.ludum22.map.bullets.Bullet;
import info.johnconrad.game.ludum22.map.logic.Logic;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.openal.Audio;

public abstract class LivingEntity extends FreeMovingTopDownEntity {
	// variables to control behavior
	protected Logic logic;
	
	protected Faction faction;
	
	protected float acceleration = 1.5f;
	protected float walkingSpeed = 4f;
	protected float runningSpeed = 6f;
	protected float maxHealth = 1;
	protected float maxShield = 99;
	protected float shieldRechargeRate = 1000;
	
	protected Class<? extends Bullet> ammoType = Bullet.class;
	protected int firingRate = 400;
	
	protected int apparationLength = 500;
	
	// state of entity
	protected float health = maxHealth;
	protected float shield = maxShield;
	protected boolean running = false;
	protected boolean born = false;
	protected boolean dead = false;
	protected int kills = 0;

	protected long nextFireTime = 0;
	protected long lastShieldCharge = 0;
	protected Vector2f lastFireDirection;
	protected Vector2f facingDirection = new Vector2f(0, 1);
	
	protected long deathTime;
	protected long birthTime;
	
	// resources used to present the entity
	protected Animation walkingAnimation = null;
	protected Animation standingAnimation = null;
	protected Animation attackAnimation = null;
	protected Animation deathAnimation = null;
	
	protected Audio attackSound = null;
	protected Audio finishJumpSound = null;
	protected Audio deathSound = null;
	
	// other junk
	static Random rand = new Random();
	
	public LivingEntity(TileMap map) {
		super(map);
		
		birthTime = GameEngine.getTime();
	}	

	@Override
	public void reset() {
		super.reset();
		
		health = maxHealth;
		shield = maxShield;
		dead = false;
		born = false;
	}
	
	public void attack(Vector2f direction) {
		if (dead || !born) return;
		
		if (!attackAnimation.isInMotion())
			attackAnimation.startAnimation(false);
		
		if (attackSound != null)
			attackSound.playAsSoundEffect(1, 1, false);
	}
	
	public void attack(LivingEntity target) {
		Vector2f attackDirection = Vector2f.sub(target.getPosition(), this.getPosition(), null);
		attackDirection.x -= target.getSizeInTiles().width * 0.5f;
		attackDirection.y -= target.getSizeInTiles().height * 0.5f;
		attack(attackDirection);
	}
	
	public void fireBullet(Vector2f direction) {
		if (dead || !born) return;
		
		if (GameEngine.getTime() < nextFireTime)
			return;
		
		nextFireTime = GameEngine.getTime() + (long) (firingRate * (rand.nextFloat() + 0.5f));
		lastFireDirection = direction;
		Vector2f startPos = new Vector2f(position);
		startPos.x += this.getSizeInTiles().width/2.0f;
		startPos.y += this.getSizeInTiles().height/2.0f;
		
		try {
			Bullet bullet = ammoType.getConstructor(TileMap.class, Vector2f.class).newInstance(map, direction);
			bullet.setOwner(this);
			bullet.setPosition(startPos);
			bullet.fireSound();
			map.addEntity(bullet);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void move(Vector2f direction) {
		if (dead || !born) return;
		
		Vector2f speedAdjustment = (Vector2f)direction.normalise(null).scale(acceleration);
		this.velocity.x += speedAdjustment.x;
		this.velocity.y += speedAdjustment.y;
		
		// limit max speed
		float max = running ? runningSpeed : walkingSpeed;
		if (velocity.length() > max)
			velocity = (Vector2f)velocity.normalise(null).scale(max);
	}
	
	public void moveTowards(TileMapEntity target) {
		if (dead || !born) return;

		Vector2f moveDirection = Vector2f.sub(target.getPosition(), this.getPosition(), null);
		moveDirection.x -= target.getSizeInTiles().width * 0.5f;
		moveDirection.y -= target.getSizeInTiles().height * 0.5f;
		move(moveDirection);
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
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
		
		/*
		float scale = calcFlashScale();
		if(flashing) {
			SpriteColor color = new SpriteColor(scaleColor(flashR, scale), scaleColor(flashG, scale), scaleColor(flashB, scale) ,scaleColor(flashA, scale));
			activeAnimation.setColor(color); 
		} else {
			activeAnimation.setColor(null);
		}
		*/
		
		float alpha = 1f;
		if (dead) alpha = 1 - ((GameEngine.getTime() - deathTime) / (float)apparationLength);
		if (!born) alpha = (GameEngine.getTime() - birthTime) / (float)apparationLength;
		
		activeAnimation.setColor(new SpriteColor(1, 1, 1, alpha));
		
		activeAnimation.setPosition(pos);
		if (attackAnimation.isInMotion())
			facingDirection = new Vector2f(lastFireDirection);
		else if (velocity.length() > 0) 
			facingDirection = new Vector2f(velocity);
		
		
		GL11.glPushMatrix();
		float ang = (float) (Vector2f.angle(facingDirection, new Vector2f(0, 1)) * 180 / Math.PI);
		if (facingDirection.x > 0) ang = -ang;
		GL11.glTranslatef(pos.x + activeAnimation.getSize().x/2, pos.y + activeAnimation.getSize().y/2, 0);
		GL11.glRotatef(ang, 0, 0, 1);
		GL11.glTranslatef(-pos.x - activeAnimation.getSize().x/2, -pos.y - activeAnimation.getSize().y/2, 0); 
		
		activeAnimation.render(timeElapsed);
		
		GL11.glPopMatrix();
		

		super.render(timeElapsed);
	}
	
	public void update(long timeElapsed) {
		super.update(timeElapsed);
		
		if (!born && birthTime + apparationLength < GameEngine.getTime()) 
			born = true;
		
		if (velocity.length() == 0) { 
			walkingAnimation.stopAnimation(false);
			standingAnimation.startAnimation(true);
		} else {
			if (!walkingAnimation.isInMotion())
				walkingAnimation.startAnimation(true);
		}
		
		if (dead && (GameEngine.getTime() - deathTime) > apparationLength) {
			scheduledForRemoval = true;
		}
		
		if (logic != null) logic.update(timeElapsed);
		
		if (shieldRechargeRate != 0 && lastShieldCharge + shieldRechargeRate < GameEngine.getTime()) {
			if (++shield > maxShield) shield = maxShield;
			lastShieldCharge = GameEngine.getTime();
		}
	}
	
	protected abstract void loadResources();

	public void damage(TileMapEntity source, float damage) {
		
		float shieldDamage = Math.min(shield, damage);
		float healthDamage = damage - shieldDamage;
		
		shield -= shieldDamage;
		health -= healthDamage * 0.5f;
		
		if (health <= 0) {
			health = 0;
			if (!dead) {
				die();
				
				if (Bullet.class.isAssignableFrom(source.getClass())) {
					((Bullet)source).getOwner().killed(this);
				}
			}
		}
		
		if (logic != null) logic.damaged(damage, source);
	}
	
	public void die() {
		dead = true;
		deathTime = GameEngine.getTime();
	}

	public float getHealth() {
		return health;
	}
	
	public float getShield() {
		return shield;
	}

	public int getKills() {
		return kills;
	}
	
	public void killed(LivingEntity victim) {
		kills++;
	}
	

	public boolean isDead() {
		return dead;
	}

	public Class<? extends Bullet> getAmmoType() {
		return ammoType;
	}

	public void setAmmoType(Class<? extends Bullet> ammoType) {
		this.ammoType = ammoType;
	}

	public Faction getFaction() {
		return faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}
}
