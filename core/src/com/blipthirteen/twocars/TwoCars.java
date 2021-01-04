package com.blipthirteen.twocars;

import com.badlogic.gdx.Game;
import com.blipthirteen.twocars.adhandler.AdHandler;
import com.blipthirteen.twocars.adhandler.MockAdHandler;
import com.blipthirteen.twocars.misc.Constants;
import com.blipthirteen.twocars.screens.GameScreen;

import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class TwoCars extends Game implements IGameServiceListener {

	private IGameServiceClient gsClient;
	private com.blipthirteen.twocars.adhandler.AdHandler handler;

	public TwoCars(IGameServiceClient gsClient, AdHandler handler, int bannerHeight){
		if(gsClient == null){
			gsClient = new NoGameServiceClient();
		}
		if(handler == null){
			handler = new MockAdHandler();
		}
		this.gsClient = gsClient;
		this.handler = handler;
		Constants.bannerHeight = bannerHeight;
	}

	@Override
	public void create () {
		this.setScreen(new GameScreen(gsClient, handler));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {

	}

	@Override
	public void gsOnSessionActive() {

	}

	@Override
	public void gsOnSessionInactive() {

	}

	@Override
	public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {

	}
}