package info.johnconrad.game.engine.tilemap.entities;

import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.TileMapTile;
import info.johnconrad.game.ludum22.map.bullets.Bullet;

import org.lwjgl.util.vector.Vector2f;

public abstract class FreeMovingTopDownEntity extends TileMapEntity {
	protected Vector2f velocity = new Vector2f(0, 0);
	protected boolean useFriction = true;

	public FreeMovingTopDownEntity(TileMap map) {
		super(map);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		velocity = new Vector2f(0, 0);
	}
	
	@Override
	public void update(long timeElapsed) {
		if (timeElapsed > 500) return;
		float scale = timeElapsed / 1000f;

		// apply friction to our current velocity if desired
		if (useFriction) applyFriction(scale);
		
		// find our new position 
		Vector2f newPosition = new Vector2f(position);
		newPosition.x += velocity.x * scale;
		newPosition.y += velocity.y * scale;

		// make sure we are not bumping into anything
		newPosition = checkTileCollisions(position, newPosition, true);
		newPosition = checkEntityCollisions(position, newPosition, true);
		position = newPosition;
	}

	@Override
	public void render(long timeElapsed) {
		super.render(timeElapsed);
	}
	
	public Vector2f checkTileCollisions(Vector2f oldPosition, Vector2f newPosition, boolean notify) {
		Vector2f correctedPosition = new Vector2f(oldPosition);
		correctedPosition.y = newPosition.y;
		BoundingBox box = getBoundingBox(correctedPosition);

		// check for y coordinate collisions
		outerloop:
		for (int x = (int)box.leftX; x < box.rightX; x++) {
			for (int y = (int)Math.floor(box.bottomY); y <= box.topY; y++) {
				TileMapTile tile = null;
				try { tile = map.getMap()[x][y]; } catch (Exception e) {}

				if (tile == null || tile.isSolid()) {
					if (Bullet.class.isAssignableFrom(this.getClass())) {
						this.scheduledForRemoval = true;
					}
					
					if (velocity.y > 0) {
						correctedPosition.y = (int) Math.ceil(oldPosition.y);
						if (notify) northCollision();
					} else {
						correctedPosition.y = (int) Math.floor(oldPosition.y);
						if (notify) southCollision();
					}

					velocity.y = 0f;
					break outerloop;
				}
			}
		}

		// move along x axis and figure our new bounds
		correctedPosition.x = newPosition.x;
		box = getBoundingBox(correctedPosition);
		
		// check for x-axis collision
		for (int x = (int) Math.floor(box.leftX); x <= box.rightX; x++) {
			for (int y = (int)box.bottomY; y < box.topY; y++) {
				TileMapTile tile = null;
				try { tile = map.getMap()[x][y]; } catch (Exception e) {}

				if (tile == null || tile.isSolid()) {
					if (velocity.x > 0) {
						correctedPosition.x = (int) Math.ceil(oldPosition.x);
						if (notify) eastCollision();
					} else {
						correctedPosition.x = (int) Math.floor(oldPosition.x);
						if (notify) westCollision();
					}
					
					if (Bullet.class.isAssignableFrom(this.getClass())) {
						if (notify) this.scheduledForRemoval = true;
					}
					

					velocity.x = 0f;
					
					break;
				}
			}
		}
		
		return correctedPosition;
	}
	
	public Vector2f checkEntityCollisions(Vector2f oldPosition, Vector2f newPosition, boolean notify) {
		Vector2f desiredPosition = new Vector2f(newPosition);
		Vector2f correctedPosition = new Vector2f(oldPosition);
		
		// check for entity collisions
		for (TileMapEntity currEntity: map.getEntities()) {
			if (this == currEntity) continue;
			boolean thisIsBullet = Bullet.class.isAssignableFrom(this.getClass());
			boolean otherIsMover = FreeMovingTopDownEntity.class.isAssignableFrom(currEntity.getClass());
			
			// check for x axis collisions
			correctedPosition.x = desiredPosition.x;
			BoundingBox thisBox = getBoundingBox(correctedPosition);
			if (thisBox.collidesWith(currEntity.getBoundingBox())) {
				if (notify) {
					this.collide(currEntity);
					currEntity.collide(this);
					
					if (!thisIsBullet && otherIsMover) {
						((FreeMovingTopDownEntity)currEntity).getVelocity().x += (newPosition.x - oldPosition.x) * 4;
					}
				}
				
				if (!thisIsBullet) {
					desiredPosition.x = oldPosition.x;
					correctedPosition.x = oldPosition.x;
				}
			}
			
			// check for y axis collisions
			correctedPosition.y = desiredPosition.y;
			thisBox = getBoundingBox(correctedPosition);
			if (thisBox.collidesWith(currEntity.getBoundingBox())) {
				if (notify) {
					this.collide(currEntity);
					currEntity.collide(this);
					
					if (!thisIsBullet && otherIsMover) {
						((FreeMovingTopDownEntity)currEntity).getVelocity().y += (newPosition.y - oldPosition.y) * 4;
					}
				}
				
				if (!thisIsBullet) {
					desiredPosition.y = oldPosition.y;
					correctedPosition.y = oldPosition.y;
				}
			}
		}
		
		return desiredPosition;
	}
	
	private void applyFriction(float scale) {
		if (Math.abs(velocity.x) > 0) {
			float sign = Math.abs(velocity.x) / velocity.x; 
			velocity.x -= sign * 9.8f * scale;
			
			// if we managed to reverse the direction of the horiz velocity, just stop
			if (velocity.x != 0 && sign != Math.abs(velocity.x) / velocity.x) {
				velocity.x = 0;
			}
		}
		
		if (Math.abs(velocity.y) > 0) {
			float sign = Math.abs(velocity.y) / velocity.y; 
			velocity.y -= sign * 9.8f * scale;
			
			// if we managed to reverse the direction of the horiz velocity, just stop
			if (velocity.y != 0 && sign != Math.abs(velocity.y) / velocity.y) {
				velocity.y = 0;
			}
		}
	}
	
	protected abstract void northCollision();
	protected abstract void southCollision();
	protected abstract void eastCollision();
	protected abstract void westCollision();
	
	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

}
