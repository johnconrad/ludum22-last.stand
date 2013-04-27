package info.johnconrad.game.ludum22.map.entities;

import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.ludum22.map.bullets.EnergyShot;
import info.johnconrad.game.ludum22.map.logic.AssaultLogic;

import java.awt.Dimension;

import org.lwjgl.util.vector.Vector2f;

public class SpiderGrunt extends SpiderGuard {

	public SpiderGrunt(TileMap map) {
		super(map);
		setAttributes();
		loadResources();

		health = maxHealth;
		shield = maxShield;

		logic = new AssaultLogic(map, this);
		((AssaultLogic)logic).setDetectionRange(15);
		
		if (walkingAnimation != null) walkingAnimation.setSize(new Vector2f(40, 40));
		if (standingAnimation != null) standingAnimation.setSize(new Vector2f(40, 40));
		if (deathAnimation != null) deathAnimation.setSize(new Vector2f(40, 40));
		if (attackAnimation != null) attackAnimation.setSize(new Vector2f(40, 40));
	}
	
	private void setAttributes() {
		sizeInTiles = new Dimension(1,1);
		acceleration = 1.0f;
		walkingSpeed = 5.0f;
		runningSpeed = 7.0f;
		
		maxHealth = 1;
		maxShield = 9;
		
		ammoType = EnergyShot.class;
		firingRate = 200;
		
		coreColor = new SpriteColor(0.247f, 0.694f, 1f, 1f);
	}
	
	@Override
	public void update(long timeElapsed) {
		super.update(timeElapsed);
		
		// for live debuging. REMOVE LATER!
		setAttributes();
	}
}
