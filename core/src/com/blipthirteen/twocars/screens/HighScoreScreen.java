package com.blipthirteen.twocars.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

public class HighScoreScreen implements Screen{

	// Buttons
    ImageButton backButton;
    

    Image trophy;
    // Scene 2D
    Stage stage;
    Skin skin;
    TextureAtlas buttonatlas;

    SpriteBatch batch;
    FitViewport fitViewport;
    StretchViewport stretchViewport;
    Timer t;
    Preferences prefs;
    Sprite redCar, blueCar;
    BitmapFont hugeFont;
    int highScore;

    private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 140;
        hugeFont = generator.generateFont(parameter);
        hugeFont.setColor(Color.WHITE);
        generator.dispose();
    }
    
    private void initPreferences(){
        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        if(!prefs.contains("highScore")){
        	prefs.putInteger("highScore", 0);
            prefs.flush();
        }
        highScore = prefs.getInteger("highScore");
    }
    
    public void show() {
        Gdx.input.setCatchBackKey(true);
        initPreferences();
        generateFont();
        batch = new SpriteBatch();
        
        loadSprites();
        
		stretchViewport = new StretchViewport(1080,1920);
        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);

        prefs = Gdx.app.getPreferences(Constants.PREF_NAME);

        skin= new Skin();
        buttonatlas = new TextureAtlas(Constants.TEXTURE_PACK);
        skin.addRegions(buttonatlas);

        backButton = new ImageButton(skin.getDrawable("back_up"),skin.getDrawable("back_down"));
        trophy = new Image(skin.getDrawable("trophy"));
        
        setButtonPostion();
        Gdx.input.setInputProcessor(stage);

        stage.addActor(trophy);
        stage.addActor(backButton);
        
        setButtonListeners();
    }

    private void loadSprites() {
    	blueCar = new Sprite(new Texture("textures/car_blue.png"));
    	blueCar.setPosition(Constants.WIDTH/2 -190 - blueCar.getWidth()/2, Constants.HEIGHT/2 - 290);
    	blueCar.setRotation(-36);
    	redCar = new Sprite(new Texture("textures/car_red.png"));
    	redCar.setPosition(Constants.WIDTH/2 + 190 - redCar.getWidth()/2, Constants.HEIGHT/2 - 290);
		redCar.setRotation(36);
    }
    
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        
//        batch.setProjectionMatrix(stretchViewport.getCamera().combined);
//        stretchViewport.apply(true);
//        batch.begin();
//        batch.end();
        
        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        
        stage.act();
        stage.draw();
        
        batch.begin();
        
        redCar.draw(batch);
        blueCar.draw(batch);
        GlyphLayout glyphLayout = new GlyphLayout();
        String score = "" + highScore;
        glyphLayout.setText(hugeFont, score);
        float w = glyphLayout.width;
        
        hugeFont.draw(batch, score, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 300);
        batch.end();

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
    	blueCar.getTexture().dispose();
    	redCar.getTexture().dispose();
        hugeFont.dispose();
    	stage.dispose();
        batch.dispose();
        skin.dispose();
        buttonatlas.dispose();
    }

    private void setButtonPostion() {
    	trophy.setPosition(1080/2 - trophy.getWidth()/2,1920/2 + 120 - trophy.getHeight()/2);
        backButton.setPosition(1080/2 - backButton.getWidth()/2,1920/2 - 550 - backButton.getHeight()/2);
    }

    private void setButtonListeners() {
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, 0.10f);
                return true;
            }
        });
    }
}