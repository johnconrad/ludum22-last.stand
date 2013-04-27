package info.johnconrad.game.ludum22.map.logic;

import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;
import info.johnconrad.game.ludum22.map.entities.LivingEntity;

public abstract class Logic {
	
	protected LivingEntity body;
	protected TileMap map;
	
	public Logic(TileMap map, LivingEntity body) {
		this.map = map;
		this.body = body;
	}
	
	public abstract void update(long timeElapsed);
	
	public abstract void damaged(float damage, TileMapEntity source);
	
	public abstract void northCollision();

	public abstract void southCollision();

	public abstract void eastCollision();

	public abstract void westCollision();
}
