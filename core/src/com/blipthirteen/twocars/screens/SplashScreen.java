package com.blipthirteen.twocars.screens;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blipthirteen.twocars.adhandler.AdHandler;
import com.blipthirteen.twocars.misc.Constants;
import com.blipthirteen.twocars.tween.SpriteAccessor;
import com.blipthirteen.twocars.tween.SpriteAccessor2;


/**
 * Created by rohit on 25-12-2016.
 */

public class SplashScreen implements Screen {

    SpriteBatch batch;
    Sprite logosprite;
    FitViewport fitViewport;
    TweenManager tm;
    Preferences prefs;

    private IGameServiceClient gsClient;
    private AdHandler handler;
    private boolean loadTutorial;

    public SplashScreen(IGameServiceClient gsClient,AdHandler handler){
        this.gsClient = gsClient;
        this.handler = handler;
    }

    public void show() {
        loadTutorial = false;
        handler.showAds(false);
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        if(!prefs.contains("default")){
            prefs.putString("default","prefs");
            prefs.putBoolean("music",true);
            prefs.putBoolean("sound",true);
            prefs.putInteger("highScore",0);
            prefs.flush();
            loadTutorial = true;
        }

        fitViewport = new FitViewport(1080, 1920);
        batch = new SpriteBatch();
        logosprite = new Sprite(new Texture("blipthirteen.png"));
        logosprite.setPosition(1080/2 - logosprite.getWidth()/2, 1920/2 - logosprite.getHeight()/2);

        tm = new TweenManager();

        Tween.registerAccessor(Sprite.class,new SpriteAccessor());

        Tween.set(logosprite, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(logosprite, SpriteAccessor.ALPHA, 1f).target(1).repeatYoyo(1, .5f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                if(loadTutorial){
                    ((Game)Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(gsClient, handler));
                }else{
                    ((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(gsClient, handler));
                }
            }
        }).start(tm);

        Tween.registerAccessor(Sprite.class,new SpriteAccessor2());
        Tween.to(logosprite, SpriteAccessor2.POSITION_XY, 2f).target(Constants.WIDTH/2 - logosprite.getWidth()/2, Constants.HEIGHT/2).ease(Elastic.INOUT).start(tm);
        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation
    }

    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1,1,1,1);
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        logosprite.draw(batch);
        batch.end();

        tm.update(delta);
    }

    public void resize(int width, int height) {
        fitViewport.update(width,height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        logosprite.getTexture().dispose();
        batch.dispose();
    }
}