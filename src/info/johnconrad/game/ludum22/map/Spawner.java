package info.johnconrad.game.ludum22.map;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;
import info.johnconrad.game.ludum22.map.entities.LivingEntity;
import info.johnconrad.game.ludum22.map.entities.SpiderGrunt;
import info.johnconrad.game.ludum22.map.entities.SpiderGuard;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

public class Spawner {
	Random rand = new Random();
	long now = 0;

	// configuration settings
	long firstWaveLength = 10000;
	long finalTimeReducedWave = 20;
	long firstGuardWave = 5;
	float spawnRadius = 15;

	// current state
	private int wave = 0;
	private long nextWaveStart = 0;
	private long lastChecked = 0;
	private boolean timeReduced = false;

	// objects we care about
	GameMap map;
	
	public Spawner(GameMap map) {
		this.map = map;
	}
	
	public void updated(long timeElapsed) {
		now = GameEngine.getTime();
		
		if (lastChecked + 500 > now) {
			lastChecked = now;
			return;
		}
		
		int activeEnemies = getActiveEnemies();
		
		if (nextWaveStart < now) {
			startWave();
			return;
		}
		
		if (!timeReduced && activeEnemies == 0) {
			nextWaveStart = (nextWaveStart + now) / 2;
			timeReduced = true;
		}
			
	}
	int total = 0;
	public void startWave() {
		now = GameEngine.getTime();
		timeReduced = false;
		long length = firstWaveLength + (firstWaveLength / finalTimeReducedWave) * wave;
		nextWaveStart = now + length; 
		
		float gruntVariance = (rand.nextFloat() * 2) - 1;
		float guardVariance = (rand.nextFloat() * 2) - 1;

		int grunts = (int)Math.round(1.6f * Math.log(wave) + 1);
		grunts += Math.round(gruntVariance * (int)Math.floor(grunts / 5));
		if (grunts <= 0) grunts = 1;
		
		int guards = Math.round((wave + firstGuardWave * guardVariance) / firstGuardWave);
		if (wave < 4) guards = 0;
		
		total += guards;
		total += grunts;
		System.out.print(String.format("Wave #%02d: GRUNTS[%02d] GUARDS[%02d] TOTAL[%02d] LENGTH[%03d]\n", wave, grunts, guards, total, length/1000));
		
		
		for (int i = 0; i < grunts; i++) 
			randomSpawn(new SpiderGrunt(map));
		
		
		for (int i = 0; i < guards; i++) 
			randomSpawn(new SpiderGuard(map));
		
		wave++;
	}
	
	private void randomSpawn(LivingEntity entity) {
		Vector2f playerPos = map.getPlayer().getPosition();
		
		Vector2f zero = new Vector2f(0, 0);
		Vector2f pos;
		int tries = 5;
		do {
			tries--;
			pos = new Vector2f(playerPos.x + (rand.nextFloat() - 0.5f) * spawnRadius, playerPos.y + (rand.nextFloat() - 0.5f) * spawnRadius);
			pos = entity.checkTileCollisions(zero, pos, false);
			pos = entity.checkEntityCollisions(zero, pos, false);
		} while (tries > 0 && (pos.x == 0 || pos.y == 0));
		
		if (tries == 0) return;
		entity.setPosition(pos);
		map.addEntity(entity);
	}
	
	public int getActiveEnemies() {
		int count = 0;
		for(TileMapEntity currEntity: map.getEntities()) {
			if ((currEntity instanceof LivingEntity) && currEntity != map.getPlayer()) {
				count++;
			}
		}
		
		return count;
	}

	public int getWave() {
		return wave;
	}
	
	
}
