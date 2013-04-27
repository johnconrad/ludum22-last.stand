package info.johnconrad.game.engine.tilemap;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.graphics.Renderable2D;
import info.johnconrad.game.engine.graphics.Sprite;
import info.johnconrad.game.engine.graphics.Sprite.SpriteColor;
import info.johnconrad.game.engine.tilemap.TileMapTile.Transition;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public abstract class TileMap extends Renderable2D {
	public static class TileProperties {
		public float tint;
		public int variant;
	}
	
	protected Random random;
	
	protected Texture artworkTexture;
	protected Texture mapTexture;
	protected byte[] rawMapData;

	protected TileMapTile[][] map;
	protected TileProperties[][] mapProperties;
	
	protected Dimension mapSize = new Dimension(0, 0);
	protected Vector2f tileSize = new Vector2f(0, 0);

	protected ArrayList<TileMapEntity> entities;
	protected ArrayList<TileMapEntity> entitiesToAdd;
	
	protected ArrayList<Class<? extends TileMapEntity>> entityTypeList;
	protected ArrayList<TileMapTile> tileList;
	protected HashMap<TileMapTile,HashMap<TileMapTile, HashMap<Transition, Renderable2D>>> transitionTiles;
	
	protected HashMap<Long, Class<? extends TileMapEntity>> entityLookup;
	protected HashMap<Long, TileMapTile> tileLookup;
	
	protected Vector2f viewPos = new Vector2f(0, 0);
	protected Dimension viewSize = new Dimension(32, 24);
	
	protected boolean drawBoundingBoxes = false;
	
	public Dimension getMapSize() {
		return mapSize;
	}

	public void setMapSize(Dimension mapSize) {
		this.mapSize = mapSize;
	}

	public Vector2f getTileSize() {
		return tileSize;
	}

	public void setTileSize(Vector2f tileSize) {
		this.tileSize = tileSize;
		updateDrawingDimensions();
	}

	public void updateDrawingDimensions() {
		for(TileMapTile currTile: tileList) { 
			if (currTile == null) continue;
			
			for(Renderable2D currArt: currTile.getArtwork()) 
				currArt.setSize(new Vector2f(tileSize.x, tileSize.y));
		}
	}

	public Vector2f getViewPos() {
		return viewPos;
	}

	public void setViewPos(Vector2f viewPos) {
		this.viewPos = new Vector2f(viewPos);
	}
	
	private void verifyViewPos() {
		if (viewPos.x < 0) viewPos.x = 0;
		if (viewPos.y < 0) viewPos.y = 0;
		
		if (viewPos.x >= mapSize.width - viewSize.width) 
			viewPos.x = mapSize.width - viewSize.width;

		if (viewPos.y >= mapSize.height - viewSize.height) 
			viewPos.y = mapSize.height - viewSize.height;		
	}

	public Dimension getViewSize() {
		return viewSize;
	}

	public void setViewSize(Dimension viewSize) {
		this.viewSize = viewSize;
	}
	
	public void calcViewSize() {
		Dimension size = new Dimension((int)(getSize().x / tileSize.x), (int)(getSize().y / tileSize.y));
		this.setViewSize(size);
 
	}

	public TileMapTile[][] getMap() {
		return map;
	}

	public TileMap() {
		random = new Random(GameEngine.getTime());
		entitiesToAdd = new ArrayList<TileMapEntity>();
	}
	
	public void resetEntities() {
		for(TileMapEntity entity: entities) {
			entity.reset();
		}
	}
	
	protected void initialize() {
		rawMapData = mapTexture.getTextureData();
		
		loadTiles();
		loadEntitiesTypes();
		loadMap();
		
		rawMapData = null;

	}
	
	public void setTileArtwork(Texture artworkTexture, int x, int y, int tileWidth, int tileHeight) {
		this.artworkTexture = artworkTexture;
		
		int imageWidth = artworkTexture.getImageWidth();
		int imageHeight = artworkTexture.getImageHeight();

		tileSize = new Vector2f(tileWidth, tileHeight);

		int variantOffset = 0;
		for(int i = 0; i < tileList.size(); i++) {
			TileMapTile currTile = tileList.get(i);
			if (currTile == null) continue;
			
			for (int j = 0; j < currTile.getVariants(); j++) {
				int imageNum = i + j + variantOffset;
				
				int row = imageNum / (imageWidth / (tileWidth + 1));
				int column = imageNum % (imageHeight / (tileHeight + 1));

				int xOffset = (tileWidth + 1) * column;
				int yOffset = (tileHeight + 1) * row;
				float s = (x + xOffset + 1) / (float) imageWidth;
				float t = (y + yOffset + 1) / (float) imageHeight;
				float r = (x + tileWidth + xOffset) / (float) imageWidth;
				float q = (y + tileHeight + yOffset) / (float) imageHeight;

				Sprite newSprite = new Sprite(artworkTexture, s, t, r, q);
				newSprite.setSize(new Vector2f(tileWidth, tileHeight));
				newSprite.setCentered(false);
				currTile.getArtwork().add(newSprite);
			}
			
			variantOffset += currTile.getVariants() - 1;
		}
	}
	
	public void setTransitionTiles(TileMapTile first, TileMapTile second, 
			                       Texture artworkTexture, int x, int y, int tileWidth, int tileHeight) {
		
		this.artworkTexture = artworkTexture;
		
		int imageWidth = artworkTexture.getImageWidth();
		int imageHeight = artworkTexture.getImageHeight();

		tileSize = new Vector2f(tileWidth, tileHeight);

		HashMap<Transition, Renderable2D> transitionArt = getTransitionTiles(first, second);
		
		int i = 0;
		for (Transition currTransition: Transition.values()) {
			int imageNum = i;

			int row = imageNum / (imageWidth / (tileWidth + 1));
			int column = imageNum % (imageHeight / (tileHeight + 1));

			int xOffset = (tileWidth + 1) * column;
			int yOffset = (tileHeight + 1) * row;
			float s = (x + xOffset + 1) / (float) imageWidth;
			float t = (y + yOffset + 1) / (float) imageHeight;
			float r = (x + tileWidth + xOffset) / (float) imageWidth;
			float q = (y + tileHeight + yOffset) / (float) imageHeight;

			Sprite newSprite = new Sprite(artworkTexture, s, t, r, q);
			newSprite.setSize(new Vector2f(tileWidth, tileHeight));
			newSprite.setCentered(false);
			transitionArt.put(currTransition, newSprite);

			i++;
		}
	}
	
	private HashMap<Transition, Renderable2D> getTransitionTiles(TileMapTile first, TileMapTile second) {
		if (transitionTiles == null) transitionTiles = new HashMap<TileMapTile, HashMap<TileMapTile,HashMap<Transition,Renderable2D>>>();
		if (!transitionTiles.containsKey(first)) transitionTiles.put(first, new HashMap<TileMapTile, HashMap<Transition,Renderable2D>>());
		if (!transitionTiles.get(first).containsKey(second)) transitionTiles.get(first).put(second, new HashMap<TileMapTile.Transition, Renderable2D>());
		
		return transitionTiles.get(first).get(second);
	}
	
	protected abstract void buildTileList();
	
	protected abstract void buildEntityList();
	
	private void loadTiles() {
		buildTileList();
		
		Queue<TileMapTile> tiles = new LinkedList<TileMapTile>(tileList);
		tileLookup = new HashMap<Long, TileMapTile>();
		
		int pos = 0;
		while (tiles.size() > 0) {
			tileLookup.put(getKey(rawMapData[pos], rawMapData[pos+1], rawMapData[pos+2]), tiles.remove());
			pos += 3;
		}
	}
	
	private void loadEntitiesTypes() {
		buildEntityList();
		
		Queue<Class<? extends TileMapEntity>> entities = new LinkedList<Class<? extends TileMapEntity>>(entityTypeList);
		entityLookup = new HashMap<Long, Class<? extends TileMapEntity>>();

		int pos = mapTexture.getTextureWidth() * 3; 
		while (entities.size() > 0) {
			Class<? extends TileMapEntity> entityType = entities.remove();
			if (TileMapEntity.class.isAssignableFrom(entityType))
				entityLookup.put(getKey(rawMapData[pos], rawMapData[pos+1], rawMapData[pos+2]), entityType);
			pos += 3;
		}
		
	}
	
	private void loadMap() {
		final int keyRows = 2;
		
		mapSize.width = mapTexture.getTextureWidth();
		int texHeight = mapTexture.getTextureHeight();
		mapSize.height = texHeight - keyRows;
		
		entities = new ArrayList<TileMapEntity>();
		map = new TileMapTile[mapSize.width][mapSize.height];
		mapProperties = new TileProperties[mapSize.width][mapSize.height];
		
		// offset of proper level data
		int offset = (mapSize.width * 3) * keyRows;
		
		for (int y = 0; y < mapSize.height; y++) {
			for(int x = 0; x < mapSize.width; x++)  {
				
				int pos = offset + (x * 3) + (y * 3) * texHeight;
				long key = getKey(rawMapData[pos], rawMapData[pos+1], rawMapData[pos+2]);
				
				TileMapTile tile = tileLookup.get(key);
				Class<? extends TileMapEntity> entityType = entityLookup.get(key);
				if (entityType != null) {
					try {
						// this position on the map was an entity.
						// so we will use the first proper tile for this location
						tile = tileList.get(1);
						
						// and create our monster or whatever and set it's position
						TileMapEntity entity = entityType.newInstance();
						entity.setPosition(new Vector2f(x, mapSize.height - y - 1));
						entity.setStartingPosition(new Vector2f(entity.getPosition()));
						entity.setMap(this);
						entities.add(entity);

					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				TileProperties prop = new TileProperties();
				prop.tint = 1f - (float)random.nextDouble() * 0.04f;
				prop.variant = random.nextInt(tile.getVariants());
				
				map[x][mapSize.height - y - 1] = tile;
				mapProperties[x][mapSize.height - y - 1] = prop;
			}
		}
	}
	
	@Override
	public void render(long timeElapsed) {
		verifyViewPos();
		
		// determine where in our tilemap we are drawing from
		Vector2f bounds = new Vector2f();
		bounds.x = Math.min(mapSize.width, viewPos.x + viewSize.width);
		bounds.y = Math.min(mapSize.height, viewPos.y + viewSize.height);
		
		// draw our tiles
		for (float y = (float) Math.floor(viewPos.y) - 1; y < bounds.y; y++) {
			for(float x = (float) Math.floor(viewPos.x) - 1; x < bounds.x; x++)  {
				if (x < 0 || y < 0) continue;
				
				TileMapTile tile = map[(int)x][(int)y];
				TileProperties prop = mapProperties[(int)x][(int)y];
				if (tile == null) continue;
				
				Renderable2D art = getArtwork((int)x, (int)y, prop.variant);//tile.getArtwork().get(prop.variant);
				Vector2f drawPos = new Vector2f(position.x + ((x - viewPos.x) * tileSize.x), 
	                                            position.y + ((y - viewPos.y) * tileSize.y));

				if (tile.isColorVaried()) 
					((Sprite)art).setColor(new SpriteColor(prop.tint, prop.tint, prop.tint, 1));
				
				art.setPosition(drawPos);
				art.render(timeElapsed);
			}
		}
		
		// draw our creepies if they are visible
		for (TileMapEntity entity: entities) {
			if (entity.getPosition().x >= viewPos.x - entity.getSizeInTiles().width && 
				entity.getPosition().x < bounds.x &&
				entity.getPosition().y >= viewPos.y - entity.getSizeInTiles().width && 
				entity.getPosition().y < bounds.y) {
			
				entity.render(timeElapsed);
			}
			
		}
	}

	private Renderable2D getArtwork(int x, int y, int variant) {
		TileMapTile mainTile = map[x][y];
		
		HashMap<TileMapTile, HashMap<Transition, Renderable2D>> transitions = transitionTiles.get(mainTile);
		if (transitions == null || transitions.size() == 0)
			return mainTile.getArtwork().get(variant);
		
		TileMapTile n = null;
		TileMapTile s = null;
		TileMapTile e = null;
		TileMapTile w = null;
		TileMapTile nw = null;
		TileMapTile ne = null;
		TileMapTile sw = null;
		TileMapTile se = null;
		
		if (x > 0) w = map[x-1][y];
		if (x < map.length - 1) e = map[x+1][y];
		if (y < map[x].length - 1) n = map[x][y+1];
		if (y > 0) s = map[x][y-1];
		
		if (x > 0 && y < map[x].length - 1) nw = map[x-1][y+1];
		if (x < map.length - 1 && y < map[x].length - 1) ne = map[x+1][y+1];
		if (y > 0 && x < map.length - 1) se = map[x+1][y-1];
		if (y > 0 && x > 0) sw = map[x-1][y-1];
		
		if (n == e && n == s && n == w) {
			Renderable2D art = null;
			if (ne != mainTile) art = getTransitionTiles(mainTile, ne).get(Transition.NORTHEAST_SMALL);
			if (nw != mainTile) art = getTransitionTiles(mainTile, nw).get(Transition.NORTHWEST_SMALL);
			if (sw != mainTile) art = getTransitionTiles(mainTile, sw).get(Transition.SOUTHWEST_SMALL);
			if (se != mainTile) art = getTransitionTiles(mainTile, se).get(Transition.SOUTHEAST_SMALL);
			if (art != null) return art;
		}

		// big corners
		if (n == e && n != mainTile && s == w && s == mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, n).get(Transition.NORTHEAST_BIG);
			if (art != null) return art;
		}

		if (s == e && s != mainTile && n == w && n == mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, s).get(Transition.SOUTHEAST_BIG);
			if (art != null) return art;
		}

		if (n == w && n != mainTile && s == e && s == mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, n).get(Transition.NORTHWEST_BIG);
			if (art != null) return art;
		}

		if (s == w && s != mainTile && n == e && n == mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, s).get(Transition.SOUTHWEST_BIG);
			if (art != null) return art;
		}
		
		// cardinal directions
		if (n == mainTile && s != mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, s).get(Transition.SOUTH);
			if (art != null) return art;
		}

		if (s == mainTile && n != mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, n).get(Transition.NORTH);
			if (art != null) return art;
		}
		
		if (e == mainTile && w != mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, w).get(Transition.WEST);
			if (art != null) return art;
		}
		
		if (w == mainTile && e != mainTile) {
			Renderable2D art = getTransitionTiles(mainTile, e).get(Transition.EAST);
			if (art != null) return art;
		}

		return mainTile.getArtwork().get(variant);		
	}
	
	@Override
	public void update(long timeElapsed) {
		// update entities
		ArrayList<TileMapEntity> entitiesToRemove = new ArrayList<TileMapEntity>();
		for (TileMapEntity entity: entities) {
			entity.update(timeElapsed);
			if (entity.isScheduledForRemoval())
				entitiesToRemove.add(entity);
		}
		
		// remove dead entities and add new ones
		entities.removeAll(entitiesToRemove);
		entities.addAll(0, entitiesToAdd);
		entitiesToAdd.clear();
	}

	public void addEntity(TileMapEntity entity) {
		entitiesToAdd.add(entity);
	}
	
	public ArrayList<TileMapEntity> getEntities() {
		return entities;
	}
	
	public boolean isDrawBoundingBoxes() {
		return drawBoundingBoxes;
	}

	public void setDrawBoundingBoxes(boolean drawBoundingBoxes) {
		this.drawBoundingBoxes = drawBoundingBoxes;
	}

	public static long getKey(byte r, byte g, byte b) {
		return (((((long)r) << 8) ^ (long)g) << 8) ^ (long)b;
	}
}
