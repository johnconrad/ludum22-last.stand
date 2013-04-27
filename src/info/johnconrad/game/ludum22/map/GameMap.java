package info.johnconrad.game.ludum22.map;

import info.johnconrad.game.engine.GameEngine;
import info.johnconrad.game.engine.tilemap.TileMap;
import info.johnconrad.game.engine.tilemap.TileMapTile;
import info.johnconrad.game.engine.tilemap.entities.TileMapEntity;
import info.johnconrad.game.ludum22.map.entities.Hero;
import info.johnconrad.game.ludum22.map.entities.LivingEntity;
import info.johnconrad.game.ludum22.map.entities.SpiderGuard;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class GameMap extends TileMap {

	public static enum Faction { HUMANS, MACHINES }

	Spawner spawner;
	LivingEntity player;
	Hero hero;
	
	boolean firstUpdate = true;
	
	public GameMap() {
		super();
		
		try {
			mapTexture = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/map.png"));
			Texture tiles = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/tiles-desaturated.png"));
			Texture actors = TextureLoader.getTexture("PNG", GameEngine.class.getResourceAsStream("/images/actors.png"));
			
			Hero.setTexture(actors);
			SpiderGuard.setTexture(actors);
			
			initialize();
			setTileArtwork(tiles, 0, 82, 40, 40);
			setTransitionTiles(tileList.get(0), tileList.get(1), tiles, 0, 0, 40, 40);
			
			placeHero();
			spawner = new Spawner(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void placeHero() {
		hero = new Hero(this);
		hero.setPosition(new Vector2f(40, 40));
		getEntities().add(hero);
		player = hero;

	}
	
	public void update(long timeElapsed) {
		super.update(timeElapsed);
		spawner.updated(timeElapsed);

		if (firstUpdate) {
			viewPos = new Vector2f();
			viewPos.x = getPlayer().getPosition().x - ((float)viewSize.width) / 2;
			viewPos.y = getPlayer().getPosition().y - ((float)viewSize.height) / 2;
			firstUpdate = false;
		}
		
		Vector2f desiredPos = new Vector2f();
		desiredPos.x = getPlayer().getPosition().x - ((float)viewSize.width) / 2;
		desiredPos.y = getPlayer().getPosition().y - ((float)viewSize.height) / 2;
		
		Vector2f desiredDirection = new Vector2f(desiredPos.x - viewPos.x, desiredPos.y - viewPos.y);
		float distance = desiredDirection.length();
		if (distance == 0) return;
		
		float scale = (distance * timeElapsed / 200) / distance;
		
		viewPos.x += desiredDirection.x * scale; 
		viewPos.y += desiredDirection.y * scale; 
	}
	
	public LivingEntity getPlayer() {
		return player;
	}

	public Spawner getSpawner() {
		return spawner;
	}

	@Override
	protected void buildTileList() {
		if (tileList != null) return;
		tileList = new ArrayList<TileMapTile>();
		
		TileMapTile grass = new TileMapTile("Grass", false, false, 6);
		grass.setColorVaried(false);
		
		TileMapTile dirt = new TileMapTile("Dirt", false, false, 6);
		dirt.setColorVaried(false);
		
		TileMapTile strangeObject = new TileMapTile("Strange Object", true, false, 1);
		dirt.setColorVaried(false);

		TileMapTile wall = new TileMapTile("Wall", true, false, 1);
		wall.setColorVaried(true);

		TileMapTile ceiling = new TileMapTile("Ceiling", true, false, 1);
		ceiling.setColorVaried(true);

		TileMapTile pavedTile = new TileMapTile("Tile", false, false, 1);
		pavedTile.setColorVaried(false);

		tileList.add(grass);
		tileList.add(dirt);
		tileList.add(strangeObject);
		tileList.add(wall);
		tileList.add(ceiling);
		tileList.add(pavedTile);

	}

	@Override
	protected void buildEntityList() {
		if (entityTypeList != null) return;
		
		entityTypeList = new ArrayList<Class<? extends TileMapEntity>>();
	}

}
