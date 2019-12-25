package com.blipthirteen.twocars.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.blipthirteen.twocars.TwoCars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height= 1920/3;
		config.width = 1080/3;
		new LwjglApplication(new TwoCars(), config);
	}
}
