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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.blipthirteen.twocars.misc.Constants;

public class HelpScreen implements Screen{

	// Buttons
    ImageButton backButton, nextButton;

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
    Sprite redDot, blueDot;
    
    BitmapFont hugeFont;
    int highScore;
    boolean showTextTwo;

    private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        hugeFont = generator.generateFont(parameter);
        hugeFont.setColor(Color.BLACK);
        generator.dispose();
    }
    
    public void show() {
    	showTextTwo = false;
        Gdx.input.setCatchBackKey(true);
        generateFont();
        batch = new SpriteBatch();
        
        loadSprites();
        
		stretchViewport = new StretchViewport(1080,1920);
        stage = new Stage();
        fitViewport = new FitViewport(1080,1920);
        stage.setViewport(fitViewport);

        prefs = Gdx.app.getPreferences("com.blipthirteen.twocars");

        skin= new Skin();
        buttonatlas = new TextureAtlas(Constants.TEXTURE_PACK);
        skin.addRegions(buttonatlas);

        backButton = new ImageButton(skin.getDrawable("back_up"),skin.getDrawable("back_down"));
        nextButton = new ImageButton(skin.getDrawable("next_up"),skin.getDrawable("next_down"));
        
        setButtonPostion();
        Gdx.input.setInputProcessor(stage);
        
        stage.addActor(nextButton);
        stage.addActor(backButton);
        
        setButtonListeners();
    }

    private void loadSprites() {
    	blueCar = new Sprite(new Texture("textures/car_blue.png"));
    	blueCar.setPosition(Constants.WIDTH/2 -190 - blueCar.getWidth()/2, Constants.HEIGHT/2 - 290);
    	redCar = new Sprite(new Texture("textures/car_red.png"));
    	redCar.setPosition(Constants.WIDTH/2 + 190 - redCar.getWidth()/2, Constants.HEIGHT/2 - 290);
		redDot = new Sprite(new Texture("textures/dot_red.png"));
		redDot.setSize(70, 70);
		blueDot = new Sprite(new Texture("textures/dot_blue.png"));
		blueDot.setSize(70, 70);
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
        if(showTextTwo) {
        	String text = "When you see 'SWITCH!'";
        	glyphLayout.setText(hugeFont, text);
        	float w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 600);
        	text = "colors swap";
        	glyphLayout.setText(hugeFont, text);
        	w = glyphLayout.width;
//        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 500);
        	text = "2/2";
        	glyphLayout.setText(hugeFont, text);
        	w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 - 430);
        }
        
        if (!showTextTwo){
        	redDot.setPosition(637, Constants.HEIGHT/2 + 330);
            redDot.draw(batch);
            blueDot.setPosition(930, Constants.HEIGHT/2 + 330);
            blueDot.draw(batch);
            
            redDot.setPosition(922, Constants.HEIGHT/2 + 230);
            redDot.draw(batch);
            blueDot.setPosition(629, Constants.HEIGHT/2 + 230);
            blueDot.draw(batch);
        	glyphLayout = new GlyphLayout();
        	String text = "Blue car has to drive over       and dodge    ";
        	glyphLayout.setText(hugeFont, text);
        	float w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 400);
        	text = "Red car has to drive over       and dodge    ";
        	glyphLayout.setText(hugeFont, text);
        	w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 300);
        	text = "1/2";
        	glyphLayout.setText(hugeFont, text);
        	w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 - 430);
        }else {
        	redDot.setPosition(930, Constants.HEIGHT/2 + 330);
            redDot.draw(batch);
            blueDot.setPosition(637, Constants.HEIGHT/2 + 330);
            blueDot.draw(batch);
            
            redDot.setPosition(629, Constants.HEIGHT/2 + 230);
            redDot.draw(batch);
            blueDot.setPosition(922, Constants.HEIGHT/2 + 230);
            blueDot.draw(batch);
        	glyphLayout = new GlyphLayout();
        	String text = "Blue car has to drive over       and dodge    ";
        	glyphLayout.setText(hugeFont, text);
        	float w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 400);
        	text = "Red car has to drive over       and dodge    ";
        	glyphLayout.setText(hugeFont, text);
        	w = glyphLayout.width;
        	hugeFont.draw(batch, text, Constants.WIDTH/2 - w/2, Constants.HEIGHT/2 + 300);
        }
        
        
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
    	backButton.setPosition(1080/2 - backButton.getWidth()/2,1920/2 - 650 - backButton.getHeight()/2);
//    	nextButton.setSize(95, 85);
    	nextButton.setTransform(true);
    	nextButton.setPosition(1080/2 - nextButton.getWidth()/2,1920/2 - 300 - nextButton.getHeight()/2);
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
        nextButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    	showTextTwo = !showTextTwo;
                    	if(!showTextTwo) {
                    		nextButton.setPosition(1080/2 - nextButton.getWidth()/2,1920/2 - 300 - nextButton.getHeight()/2);
                    	}else {
                    		nextButton.setPosition(1080/2 + nextButton.getWidth()/2, 1920/2 -168 - nextButton.getHeight()/2);
                    	}
                    	nextButton.setRotation(nextButton.getRotation() + 180);
//                    	nextButton.rotateBy(180);
                return true;
            }
        });
    }
}
