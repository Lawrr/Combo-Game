package com.syurba.combogame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.syurba.combogame.ComboGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Block Game";
		config.width = 640;
		config.height = 960;

		new LwjglApplication(new ComboGame(), config);
	}
}
