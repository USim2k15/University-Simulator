package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.USim2k15;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.x = -1;
		config.y = -1;
		config.width = 1280;
		config.height = 720;
		config.fullscreen = false;
		
		config.title = "University Simulator";
		
		new LwjglApplication(new USim2k15(), config);
	}
}