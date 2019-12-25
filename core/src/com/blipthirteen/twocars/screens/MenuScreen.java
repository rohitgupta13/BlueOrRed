package com.blipthirteen.twocars.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.blipthirteen.twocars.misc.Constants;

/**
 * Created by rohit on 25-12-2016.
 */
public class MenuScreen implements Screen{

    // Buttons
    ImageButton playButton, hsButton, helpButton, moreGamesButton;

    // Scene 2D
    Stage stage;
    Skin skin;
    TextureAtlas buttonatlas;
    Image image;

    SpriteBatch batch;
    FitViewport fitViewport;
    StretchViewport stretchViewport;
    Timer t;
    Sprite redStrip, blueStrip, redCar, blueCar;
    float redCarPos, blueCarPos;

    public void show() {
    	redCarPos = Constants.WIDTH + 200;
		blueCarPos = -1000;
    	
        Gdx.input.setCatchBackKey(true);

        batch = new SpriteBatch();
        
        loadSprites();
        
		stretchViewport = new StretchViewport(1080,1920);
        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);


        skin= new Skin();
        buttonatlas = new TextureAtlas(Constants.TEXTURE_PACK);
        skin.addRegions(buttonatlas);

        playButton = new ImageButton(skin.getDrawable("play_up"),skin.getDrawable("play_down"));
        hsButton = new ImageButton(skin.getDrawable("hs_up"),skin.getDrawable("hs_down")); 
        helpButton = new ImageButton(skin.getDrawable("help_up"),skin.getDrawable("help_down"));
        moreGamesButton = new ImageButton(skin.getDrawable("mg_up"),skin.getDrawable("mg_down"));
        
        
        image = new Image(skin.getDrawable("title"));

        setButtonPostion();
        Gdx.input.setInputProcessor(stage);

        stage.addActor(image);
        stage.addActor(playButton);
        stage.addActor(hsButton);
        stage.addActor(helpButton);
        stage.addActor(moreGamesButton);        
        setButtonListeners();
    }

    private void loadSprites() {
    	blueStrip = new Sprite(new Texture("textures/blue_stripe.png"));
    	blueStrip.setPosition(0, 1920/2 - blueStrip.getHeight()/2);
    	redStrip = new Sprite(new Texture("textures/red_stripe.png"));
    	redStrip.setPosition(0, 1920/2 - redStrip.getHeight()/2 - blueStrip.getHeight());
    	blueCar = new Sprite(new Texture("textures/car_blue.png"));
    	blueCar.setRotation(-90);
		redCar = new Sprite(new Texture("textures/car_red.png"));
		redCar.setRotation(90);
    }
    
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);

        redCarPos += -350 * delta;
        blueCarPos += 350 * delta;
        
        if(redCarPos < -200 -redCar.getWidth()) {
        	redCarPos = Constants.WIDTH + 600;
        }
        
        if(blueCarPos > Constants.WIDTH + 200) {
        	blueCarPos = -300 - blueCar.getWidth();
        }
        
        
        batch.setProjectionMatrix(stretchViewport.getCamera().combined);
        stretchViewport.apply(true);
        batch.begin();
        
        
        blueStrip.draw(batch);
        redStrip.draw(batch);
        redCar.setPosition(redCarPos, 1920/2 - redStrip.getHeight()/2 - blueStrip.getHeight() - 60);
        redCar.draw(batch);
        blueCar.setPosition(blueCarPos,  1920/2 - blueStrip.getHeight()/2 - 60);
        blueCar.draw(batch);
        
        batch.end();
        
        
        
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        
        
        batch.begin();
       
        
        batch.end();

        stage.act();
        stage.draw();

        if(Gdx.input.isKeyPressed(Input.Keys.BACK)){
            Gdx.app.exit();
        }
    }

    public void resize(int width, int height) {
    	stretchViewport.update(width,height,true);
        fitViewport.update(width,height,true);
        stage.getViewport().update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
    	blueStrip.getTexture().dispose();
    	redStrip.getTexture().dispose();
    	blueCar.getTexture().dispose();
    	redCar.getTexture().dispose();
        stage.dispose();
        batch.dispose();
        skin.dispose();
        buttonatlas.dispose();
    }

    private void setButtonPostion() {
        image.setPosition(1080/2 - image.getWidth()/2, 1920/2 + 500 - image.getHeight()/2);
        playButton.setPosition(1080/2 - playButton.getWidth()/2,1920/2 - 550 - playButton.getHeight()/2);
        hsButton.setPosition(Constants.WIDTH/2 - hsButton.getWidth()/2 + 250, 200);
        moreGamesButton.setPosition(Constants.WIDTH/2 - moreGamesButton.getWidth()/2 - 250, 200);
        helpButton.setPosition(Constants.WIDTH/2 - helpButton.getWidth()/2, 90);
    }

    private void setButtonListeners() {
        playButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
                    }
                }, 0.10f);

                return true;
            }
        });
        
        hsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new HighScoreScreen());
                    }
                }, 0.10f);

                return true;
            }
        });
        
        helpButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new HelpScreen());
                    }
                }, 0.10f);

                return true;
            }
        });
        
        moreGamesButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.net.openURI("https://play.google.com/store/apps/developer?id=blipthirteen");
                return true;
            }
        });
    }
}