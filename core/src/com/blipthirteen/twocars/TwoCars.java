package com.blipthirteen.twocars;

import com.badlogic.gdx.Game;
import com.blipthirteen.twocars.screens.MenuScreen;

public class TwoCars extends Game {
	
	@Override
	public void create () {
		this.setScreen(new MenuScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		
	}
}
