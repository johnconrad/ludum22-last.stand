package info.johnconrad.game.engine.tilemap;

import info.johnconrad.game.engine.graphics.Renderable2D;

import java.util.ArrayList;

public class TileMapTile {
	public static enum Transition {
		NORTHEAST_BIG, WEST, NORTH, EAST, SOUTH, SOUTHEAST_SMALL, SOUTHWEST_SMALL, NORTHWEST_SMALL, NORTHEAST_SMALL,
		SOUTHEAST_BIG, SOUTHWEST_BIG, NORTHWEST_BIG
	}
	
	String name;
	boolean solid;
	boolean dangerous;
	
	int variants;
	boolean colorVaried = true;

	boolean platform = false;

	ArrayList<Renderable2D> artwork;

	public TileMapTile(String name, boolean solid, boolean dangerous) {
		this(name, solid, dangerous, 1);
	}
	
	public TileMapTile(String name, boolean solid, boolean dangerous, int variants) {
		this.name = name;
		this.solid = solid;
		this.dangerous = dangerous;
		this.variants = variants;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public boolean isDangerous() {
		return dangerous;
	}

	public void setDangerous(boolean dangerous) {
		this.dangerous = dangerous;
	}

	public int getVariants() {
		return variants;
	}

	public boolean isColorVaried() {
		return colorVaried;
	}

	public void setColorVaried(boolean colorVaried) {
		this.colorVaried = colorVaried;
	}

	public boolean isPlatform() {
		return platform;
	}

	public void setPlatform(boolean platform) {
		this.platform = platform;
	}

	public ArrayList<Renderable2D> getArtwork() {
		if (artwork == null) artwork = new ArrayList<Renderable2D>();
		return artwork;
	}
}


