package info.johnconrad.game.ludum22.map.bullets;

import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMap;

import org.lwjgl.util.vector.Vector2f;

public class PulseRifle extends Bullet {
	public static final float firingStrength = 3f;
	public static final int firingSpeed = 35;
	public static final int firingRange = 20;

	public PulseRifle(TileMap map, Vector2f direction) {
		super(map, direction, firingStrength, firingSpeed, firingRange);

		color = new SpriteColor(0.247f, 1f, 0.694f, 1f); 
		radius = 0.2f;
	}
}
