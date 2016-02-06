package com.syurba.combogame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ComboGame extends Game {
	public static int screenWidth = 640;
	public static int screenHeight = 960;

	public SpriteBatch batch;
	public BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
        super.render();
	}

	@Override
	public void dispose () {
		// Dispose of all the native resources
		batch.dispose();
		font.dispose();
	}
}
