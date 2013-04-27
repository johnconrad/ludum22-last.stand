package info.johnconrad.game.ludum22.widgets;

import info.johnconrad.game.engine.graphics.AngelCodeFont;
import info.johnconrad.game.engine.graphics.Renderable2D;
import info.johnconrad.game.engine.graphics.Sprite;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

public class CounterWidget extends Renderable2D {
	private static final int PADDING = 4;
	
	Texture textures;
	
	Sprite bgSprite;
	AngelCodeFont font;
	
	int count = 0;
	
	public CounterWidget(Texture textures, int x, int y) {
		try {
			this.textures = textures;
			setSize(new Vector2f(88, 48));
			bgSprite = new Sprite(textures, x, y, x+80, y+40);
			font = new AngelCodeFont("/font/inconsolata-25.fnt", "/font/inconsolata-25_0.png", false, 1f);
			
			setPosition(new Vector2f(0,0));
			
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		
		Vector2f armorPos = new Vector2f(pos);
		armorPos.x += PADDING;
		armorPos.y += PADDING;
		bgSprite.setPosition(armorPos);
	}
	
	@Override
	public void render(long timeElapsed) {
		bgSprite.render(timeElapsed);
		
		String countStr = String.format("%02d", count);
		int drawHeight = font.getHeight(countStr);
		int offset = (int)((size.y - drawHeight)/2);
		font.drawString(position.x + PADDING + 40, position.y + drawHeight + offset, countStr, timeElapsed);
	}

	@Override
	public void update(long timeElapsed) {
		bgSprite.update(timeElapsed);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
