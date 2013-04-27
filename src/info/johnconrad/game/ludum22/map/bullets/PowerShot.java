package info.johnconrad.game.ludum22.map.bullets;

import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;

import org.lwjgl.util.vector.Vector2f;

public class PowerShot extends Bullet {
	private static final float firingStrength = 7f;
	private static final int firingSpeed = 30;
	private static final int firingRange = 20;

	public PowerShot(TileMap map, Vector2f direction) {
		super(map, direction, firingStrength, firingSpeed, firingRange);

		color = new SpriteColor(1f, 0.694f, 0.247f, 1f);
		radius = 1f; 
		soundEffect = heavyLaserSound;
	}

}
