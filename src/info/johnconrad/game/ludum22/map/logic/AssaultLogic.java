package info.johnconrad.game.ludum22.map.logic;

import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;
import info.johnconrad.game.ludum22.map.bullets.Bullet;
import info.johnconrad.game.ludum22.map.entities.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector2f;

public class AssaultLogic extends Logic {
	private static class Aggressor implements Comparable<Aggressor> {
		public LivingEntity entity;
		public float damage;
		
		public Aggressor(LivingEntity entity, float damage) {
			this.entity = entity;
			this.damage = damage;
		}
		
		@Override
		public int compareTo(Aggressor obj) {
			if (this.damage == obj.damage) {
				return 0;
			} else if (this.damage > obj.damage) {
				return -1;
			}
			return 0;
		}
	}
	
	protected float detectionRange = 100;
	protected float chaseRange = 15;
	protected float engageRange = 5;

	ArrayList<Aggressor> aggressors;
	HashMap<LivingEntity, Aggressor> aggressorLookup;

	LivingEntity target = null;
	
	public AssaultLogic(TileMap map, LivingEntity body) {
		super(map, body);
		
		aggressors = new ArrayList<Aggressor>();
		aggressorLookup = new HashMap<LivingEntity, AssaultLogic.Aggressor>();
	}

	@Override
	public void update(long timeElapsed) {
		if (timeElapsed == 0)
			return;
		
		Collections.sort(aggressors);
		
		// forget about dead things
		while(aggressors.size() > 0 && aggressors.get(0).entity.isDead()) {
			aggressors.remove(0);
		}
		
		findFactionTarget();
		if (target == null) findAggressorTarget();
		if (target == null) return;
		
		float distance = Vector2f.sub(target.getPosition(), body.getPosition(), null).length();
		if (distance < chaseRange && distance > engageRange)
			body.moveTowards(target);
		
		if (distance < Bullet.getInstance(body.getAmmoType()).getRange()) {
			body.attack(target);
		}
	}
	
	private void findFactionTarget() {
		// loop through all the stuff out there
		for(TileMapEntity currEntity: map.getEntities()) {
			// if we found something alive
			if (currEntity instanceof LivingEntity) {
				// and we hate it
				LivingEntity thing = (LivingEntity)currEntity;
				if (body.getFaction() != thing.getFaction()) {
					// and it's inside detection range, engage!
					float distance = Vector2f.sub(thing.getPosition(), body.getPosition(), null).length();
					if (distance < detectionRange) {
						target = thing;
						return;
					}
				}
			}
		}
	}
	
	private void findAggressorTarget() {
		if (aggressors.size() == 0) return;
		target = aggressors.get(0).entity;
	}

	@Override
	public void damaged(float damage, TileMapEntity source) {
		if (source.getClass() == Bullet.class) {
			LivingEntity owner = ((Bullet)source).getOwner();
			
			if (!aggressorLookup.containsKey(owner)) {
				Aggressor battleRecord = new Aggressor(owner, damage);
				aggressorLookup.put(owner, battleRecord);
				aggressors.add(battleRecord);
			} else {
				aggressorLookup.get(owner).damage += damage;
			}
		}
	}
	
	@Override
	public void northCollision() {}

	@Override
	public void southCollision() {}

	@Override
	public void eastCollision() {}

	@Override
	public void westCollision() {}

	public float getDetectionRange() {
		return detectionRange;
	}

	public void setDetectionRange(float detectionRange) {
		this.detectionRange = detectionRange;
	}

	public float getChaseRange() {
		return chaseRange;
	}

	public void setChaseRange(float chaseRange) {
		this.chaseRange = chaseRange;
	}

	public float getEngageRange() {
		return engageRange;
	}

	public void setEngageRange(float engageRange) {
		this.engageRange = engageRange;
	}
}
