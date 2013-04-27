package info.johnconrad.game.engine.tilemap.entities;

import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.TileMapTile;

import org.lwjgl.util.vector.Vector2f;

public abstract class FreeMovingSideScrollerEntity extends TileMapEntity {
	static final float TERMINAL_VELOCITY = -55;
	
	protected Vector2f velocity = new Vector2f(0, 0);

	public FreeMovingSideScrollerEntity(TileMap map) {
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
		applyGravity(scale);
		move(scale);
	}

	@Override
	public void render(long timeElapsed) {
		super.render(timeElapsed);
	}
	
	public void move(float scale) {
		Vector2f oldPosition = new Vector2f(position);
		
		// make our move
		position.y += velocity.y * scale;
		
		// figure our new bounds
		BoundingBox box = getBoundingBox();

		// check for floor collision
		int y = (int)Math.floor(box.bottomY);
		for (int x = (int) Math.floor(box.leftX); x < Math.ceil(box.rightX); x++) {
			TileMapTile tile = null;
			try { tile = map.getMap()[x][y]; } catch (Exception e) {}
			
			if (tile != null && velocity.y <= 0) {
				
				WalkingSideScrollerEntity walker = this instanceof WalkingSideScrollerEntity ? (WalkingSideScrollerEntity) this : null;
				if (walker != null && !walker.dead && tile.isDangerous()) {
					((WalkingSideScrollerEntity)this).damage(5);
					position.y = (int)oldPosition.y;
					velocity.y = 15f;
					velocity.x = -0.5f * velocity.x;
					break;
				}

				if (tile.isSolid() || tile.isPlatform()) {
					if (velocity.y < -1) {
						floorCollision();
						System.out.println(this.getClass().getSimpleName() + ": " + box + " " + this.position);
					}

					position.y = (int)oldPosition.y;
					velocity.y = 0f;
					
					// apply friction
					if (Math.abs(velocity.x) > 0) {
						float sign = Math.abs(velocity.x) / velocity.x; 
						velocity.x -= sign * 9.8 * scale;
						
						// if we managed to reverse the direction of the horiz velocity, just stop
						if (velocity.x != 0 && sign != Math.abs(velocity.x) / velocity.x) {
							velocity.x = 0;
						}
					}
					break;
				}
			}
		}

		position.x += velocity.x * scale;

		
		// figure our new bounds
		box = getBoundingBox();
		
		// check for wall collision
		for (int x = (int)box.leftX; x <= box.rightX; x++) {
			for ( y = (int)box.bottomY; y < box.topY; y++) {
				TileMapTile tile = null;
				try { tile = map.getMap()[x][y]; } catch (Exception e) {}

				if (tile != null && tile.isSolid()) {
					wallCollision();

					//System.out.print(" WALL [" + x + ", " + y + "]");
					
					if (velocity.x > 0)
						position.x = (int) Math.ceil(oldPosition.x);
					else
						position.x = (int) Math.floor(oldPosition.x);

					velocity.x = 0f;
					
					break;
				}
			}
		}
	}
	
	public void applyGravity(float scale) {
		velocity.y -= 9.8 * 4 * scale;
		if (velocity.y < TERMINAL_VELOCITY) velocity.y = TERMINAL_VELOCITY;
	}
	
	// called when the object collides with the floor. can be extended by base classes
	protected void floorCollision() {
		
	}

	// called when the object collides with the wall. can be extended by base classes
	protected void wallCollision() {
		
	}

	
	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	
}
