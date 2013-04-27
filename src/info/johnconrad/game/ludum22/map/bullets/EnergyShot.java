package info.johnconrad.game.ludum22.map.bullets;

import info.johnconrad.game.engine.tilemap.TileMap;

import org.lwjgl.util.vector.Vector2f;

public class EnergyShot extends Bullet {
	private static final float firingStrength = 0.5f;
	private static final int firingSpeed = 30;
	private static final int firingRange = 20;

	public EnergyShot(TileMap map, Vector2f direction) {
		super(map, direction, firingStrength, firingSpeed, firingRange);

	}
}
