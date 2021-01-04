package com.blipthirteen.twocars.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.blipthirteen.twocars.adhandler.AdHandler;
import com.blipthirteen.twocars.misc.Constants;
import com.blipthirteen.twocars.objects.Car;
import com.blipthirteen.twocars.objects.Dot;
import com.blipthirteen.twocars.tween.SpriteAccessor2;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import de.golfgl.gdxgamesvcs.IGameServiceClient;


public class TutorialScreen implements Screen, InputProcessor{


    ImageButton homeButton, playButton, replayTutButton, backButton, skipButton;
    Stage stage;
    Skin skin;
    TextureAtlas buttonatlas;
    Image overlay;

    private Map<Integer, Float> positionMap;
    private Map<Integer, Float> dotPositionMap;


    private List<Dot> dots0;

    private FitViewport fitViewport;
    private ScreenViewport screenViewport;
    private Texture carRed, carBlue;
    private Texture stripe, solidLine;
    private Texture dotRed, dotBlue;
    private Texture instructions;
    private Sprite toStartMessage;
    private Texture roadBg, stretchTexture;

    private Car carLeft, carRight;
    private SpriteBatch batch;

    // Let's make the road scroll
    private float roadYPos, scrollSpeed;

    private boolean moveLeftCar, moveRightCar;
    private float rightCarSpeed, leftCarSpeed;


    private Color gray, currentColor;

    private BitmapFont smallFont, font, hugeFont;

    private boolean showInstructions;
    private Sprite spriteSolid, spriteStripe;
    private Sprite warning, switchSprite, switchBackSprite;
    private Sprite tapToContinue;

    private TweenManager tm;

    private IGameServiceClient gsClient;
    private AdHandler handler;

    private int tutorialStep = -1;
    private boolean nextStep;
    private String instructionText = "";
    private int distanceBetweenCars;

    public TutorialScreen(IGameServiceClient gsClient, AdHandler handler) {
        this.handler = handler;
        this.gsClient = gsClient;
    }

    @Override
    public void show() {
        handler.showAds(false);
        showInstructions = true;
        nextStep = false;

        gray = new Color((float)19/255, (float)19/255, (float)19/255, 1);
        currentColor = gray;

        roadYPos = 0;
        scrollSpeed = 900;
        distanceBetweenCars = 400;

        batch = new SpriteBatch();
        loadTextures();
        initViewports();
        populateMaps();

        generateFont();
        init();
        initScene2D();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(multiplexer);



        toStartMessage.setPosition(Constants.WIDTH/2 - toStartMessage.getWidth()/2, Constants.HEIGHT/2 - toStartMessage.getHeight()/2 -400);
        warning.setScale(.25f);
        warning.setPosition(Constants.WIDTH/2 - warning.getWidth()/2, Constants.HEIGHT/2 - warning.getHeight()/2 + 160);
        tapToContinue.setPosition(Constants.WIDTH/2 - tapToContinue.getWidth()/2, Constants.HEIGHT/2 - tapToContinue.getHeight()/2 -400);
        tapToContinue.setScale(0.85f);

        switchSprite.setPosition(Constants.WIDTH/2 - switchSprite.getWidth()/2,  Constants.HEIGHT/2 - switchSprite.getHeight()/2 + 350);
        tm = new TweenManager();
        Tween.registerAccessor(Sprite.class,new SpriteAccessor2());
        //Tween.set(toStartMessage, SpriteAccessor.ALPHA).target(0).start(tm);
        //Tween.to(toStartMessage, SpriteAccessor2.SCALE_XY, 2).target(2,2).ease(Linear.INOUT).repeat(10,0).start(tm);
        //Timeline.createSequence()
        // First, set all objects to their initial positions
        //.push(
        //Tween.from(toStartMessage, SpriteAccessor2.SCALE_XY, .5f).target(0.25f,1);
        //.push(
        Tween.to(toStartMessage, SpriteAccessor2.SCALE_XY, 1).target(.9f,.9f).repeatYoyo(-1,0).start(tm);
        Tween.to(tapToContinue, SpriteAccessor2.SCALE_XY, 1).target(0.7f,0.7f).repeatYoyo(-1,0).start(tm);
        Tween.to(warning, SpriteAccessor2.SCALE_XY, .85f).target(.31f,.31f).repeatYoyo(-1,0).start(tm);


        //Tween.from(toStartMessage, SpriteAccessor2.SCALE_XY, 2).target(1,1).start(tm);

        //Tween.to(toStartMessage, SpriteAccessor.ALPHA, 1.5f).target(1).start(tm);
        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation
    }


    private void initScene2D(){

        stage = new Stage();
        stage.setViewport(fitViewport);
        skin= new Skin();
        buttonatlas = new TextureAtlas(Constants.TEXTURE_PACK);
        skin.addRegions(buttonatlas);

        homeButton = new ImageButton(skin.getDrawable("home_inv_up"), skin.getDrawable("home_inv_down"));
        homeButton.setPosition(Constants.WIDTH/2 - homeButton.getWidth()/2 - 150, Constants.HEIGHT/2 - 300);
        homeButton.setVisible(false);

        playButton = new ImageButton(skin.getDrawable("play_inv_up"), skin.getDrawable("play_inv_down"));
        playButton.setPosition(Constants.WIDTH/2 - playButton.getWidth()/2, Constants.HEIGHT/2 - playButton.getHeight()/2);
        playButton.setVisible(false);

        replayTutButton = new ImageButton(skin.getDrawable("replay_tut_up"), skin.getDrawable("replay_tut_down"));
        replayTutButton.setPosition(Constants.WIDTH/2 - replayTutButton.getWidth()/2 + 150, Constants.HEIGHT/2 - 300);
        replayTutButton.setVisible(false);



        skipButton = new ImageButton(skin.getDrawable("skip_up"), skin.getDrawable("skip_down"));
        skipButton.setPosition(Constants.WIDTH - skipButton.getWidth()/2 - 130, Constants.HEIGHT - skipButton.getHeight()/2 - 120);

        backButton = new ImageButton(skin.getDrawable("home_inv_up"), skin.getDrawable("home_inv_down"));
        backButton.setSize(skipButton.getWidth(), skipButton.getHeight());
        backButton.setPosition(Constants.WIDTH - backButton.getWidth()*2 - 80, Constants.HEIGHT - backButton.getHeight()/2 - 120);

        overlay = new Image(skin.getDrawable("cover"));
        overlay.setSize(Constants.WIDTH, Constants.HEIGHT);
        overlay.setVisible(false);


        stage.addActor(skipButton);
        stage.addActor(backButton);
        stage.addActor(overlay);
        stage.addActor(homeButton);
        stage.addActor(replayTutButton);
        stage.addActor(playButton);
        //Gdx.input.setInputProcessor(stage);

        setButtonListeners();
    }


    private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 120;
        hugeFont = generator.generateFont(parameter);
        hugeFont.setColor(Color.WHITE);
        parameter.size = 60;
        parameter.borderWidth = 1f;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        font.setColor(Color.WHITE);
        parameter.size = 90;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.WHITE);
        generator.dispose();
    }

    private void init() {
        dots0 = new ArrayList<Dot>();
//        for (int i = 0; i < 5; i++) {
//            Dot d  = new Dot(new Vector2(dotPositionMap.get(0),1900 + i * 400), "blue", dotBlue, 70);
//            dots0.add(d);
//        }

        carLeft = new Car("carLeft", Constants.CAR_LEFT_P1, 80, carRed);
        carLeft.setSize(150, 279);
        carRight = new Car("carRight", Constants.CAR_RIGHT_P1, 80, carBlue);
        carRight.setSize(150, 279);
    }

    @Override
    public void render(float delta) {
        //Gdx.gl.glClearColor(currentColor.r, currentColor.g, currentColor.b, 1);
        Gdx.gl.glClearColor(0,1,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        roadYPos -= scrollSpeed * delta;
        if(roadYPos < -1536) {
            roadYPos = 0;
        }
        batch.setProjectionMatrix(screenViewport.getCamera().combined);
        screenViewport.apply(true);
        batch.begin();
        for (int i = 0; i < Gdx.graphics.getWidth() + stretchTexture.getWidth() * 2; i+= stretchTexture.getWidth()) {
            for (float j = 0; j < Gdx.graphics.getHeight() + stretchTexture.getHeight() * 2; j+= stretchTexture.getHeight()) {
                batch.draw(stretchTexture, i, j);
            }
        }
        batch.end();

        batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        batch.draw(roadBg, 0,0);
        // Draw a line on the left side
        spriteSolid.setPosition(40 - solidLine.getWidth()/2, 0);
        spriteSolid.draw(batch);
        // Draw a line on the right side
        spriteSolid.setPosition(1080 - 40 - solidLine.getWidth()/2, 0);
        spriteSolid.draw(batch);

        // Striped lines through the centre
        spriteStripe.setPosition(540 - stripe.getWidth()/2, roadYPos);
        spriteStripe.draw(batch);
        spriteStripe.setPosition(540 - stripe.getWidth()/2, roadYPos + stripe.getHeight());
        spriteStripe.draw(batch);
        spriteStripe.setPosition(540 - stripe.getWidth()/2, roadYPos + stripe.getHeight() * 2);
        spriteStripe.draw(batch);

        fitViewport.setScreenY(fitViewport.getScreenY() - fitViewport.getBottomGutterHeight());


        if(showInstructions) {
            toStartMessage.draw(batch);
        }
        tm.update(delta);

        for (Dot dot : dots0) {
            if (dot.isDraw()) {
                dot.draw(batch);
            }
        }

        switch(tutorialStep) {
            case -1:
                GlyphLayout glyphLayout0 = new GlyphLayout();
                glyphLayout0.setText(smallFont, "Instructions");
                float textWidth0 = glyphLayout0.width;
                smallFont.draw(batch, "Instructions", Constants.WIDTH/2 - textWidth0/2, Constants.HEIGHT/2);
                break;
            case 0:
                carLeft.setPosition(positionMap.get(tutorialStep), carLeft.getPosition().y);
                carLeft.draw(batch);
                instructionText = "Red car has to drive over the blue dots";
                break;
            case 1:
                carLeft.setPosition(positionMap.get(tutorialStep - 1), carLeft.getPosition().y);
                carLeft.draw(batch);
                instructionText = "...and dodge the red dots";
                break;

            case 2:
                carRight.setPosition(positionMap.get(tutorialStep), carLeft.getPosition().y);
                carRight.draw(batch);
                instructionText = "Blue car has to drive over the red dots";
                break;

            case 3:
                carRight.setPosition(positionMap.get(tutorialStep - 1), carLeft.getPosition().y);
                carRight.draw(batch);
                instructionText = "...and dodge the blue dots";
                break;
            case 4:
                String text = "When you see the above warning,";
                GlyphLayout glyphLayout = new GlyphLayout();
                glyphLayout.setText(font, text);
                float textWidth = glyphLayout.width;
                font.draw(batch, text, Constants.WIDTH/2 - textWidth/2, Constants.HEIGHT/2);
                text = "the objectives will change";
                glyphLayout.setText(font, text);
                textWidth = glyphLayout.width;
                font.draw(batch, text, Constants.WIDTH/2 - textWidth/2, Constants.HEIGHT/2 - 100);

                warning.draw(batch);
                switchSprite.draw(batch);

                break;
            case 5:
                carRight.setPosition(positionMap.get(2), carLeft.getPosition().y);
                carRight.draw(batch);
                carLeft.setPosition(positionMap.get(0), carLeft.getPosition().y);
                carLeft.draw(batch);
                for (int i = 0; i < dots0.size(); i++) {
                    System.out.println("Info: " +  dots0.get(i).getXPosition() + ", "+ dotPositionMap.get(0));
                    if(dots0.get(i).getYPosition() > 300 && (dots0.get(i).getXPosition() == dotPositionMap.get(0) || dots0.get(i).getXPosition() == dotPositionMap.get(2))){
                        dots0.get(i).getBoundingCircle().y -= delta * scrollSpeed;
                    }else if(dots0.get(i).getYPosition() > -100 && (dots0.get(i).getXPosition() == dotPositionMap.get(1) || dots0.get(i).getXPosition() == dotPositionMap.get(3))){
                        dots0.get(i).getBoundingCircle().y -= delta * scrollSpeed;
                    }
                    else{
                        dots0.remove(i);
                    }
                }

                String text1 = "In switch mode";
                GlyphLayout glyphLayout1 = new GlyphLayout();
                glyphLayout1.setText(font, text1);
                float textWidth1 = glyphLayout1.width;
                font.draw(batch, text1, Constants.WIDTH/2 - textWidth1/2, Constants.HEIGHT/2);
                text1 = "Red car has to drive over red dots, and dodge blue dots";
                glyphLayout1.setText(font, text1);
                textWidth1 = glyphLayout1.width;
                font.draw(batch, text1, Constants.WIDTH/2 - textWidth1/2, Constants.HEIGHT/2 - 100);
                warning.draw(batch);
                break;
            case 6:
                carRight.setPosition(positionMap.get(2), carLeft.getPosition().y);
                carRight.draw(batch);
                carLeft.setPosition(positionMap.get(0), carLeft.getPosition().y);
                carLeft.draw(batch);
                for (int i = 0; i < dots0.size(); i++) {
                    System.out.println("Info: " +  dots0.get(i).getXPosition() + ", "+ dotPositionMap.get(0));
                    if(dots0.get(i).getYPosition() > 300 && (dots0.get(i).getXPosition() == dotPositionMap.get(0) || dots0.get(i).getXPosition() == dotPositionMap.get(2))){
                        dots0.get(i).getBoundingCircle().y -= delta * scrollSpeed;
                    }else if(dots0.get(i).getYPosition() > -100 && (dots0.get(i).getXPosition() == dotPositionMap.get(1) || dots0.get(i).getXPosition() == dotPositionMap.get(3))){
                        dots0.get(i).getBoundingCircle().y -= delta * scrollSpeed;
                    }
                    else{
                        dots0.remove(i);
                    }
                }

                String text2 = "In switch mode";
                GlyphLayout glyphLayout2 = new GlyphLayout();
                glyphLayout2.setText(font, text2);
                float textWidth2 = glyphLayout2.width;
                font.draw(batch, text2, Constants.WIDTH/2 - textWidth2/2, Constants.HEIGHT/2);
                text1 = "Blue car has to drive over blue dots, and dodge red dots";
                glyphLayout2.setText(font, text1);
                textWidth2 = glyphLayout2.width;
                font.draw(batch, text1, Constants.WIDTH/2 - textWidth2/2, Constants.HEIGHT/2 - 100);
                warning.draw(batch);
                break;
            default:


        }
        if(tutorialStep < 5){
            for (int i = 0; i < dots0.size(); i++) {
                if(dots0.get(i).getYPosition() > 300 && (tutorialStep == 0  || tutorialStep == 2)){
                    dots0.get(i).getBoundingCircle().y -= delta * scrollSpeed;
                }else if(dots0.get(i).getYPosition() > -100 && (tutorialStep == 1  || tutorialStep == 3)){
                    dots0.get(i).getBoundingCircle().y -= delta * scrollSpeed;
                }
                else{
                    dots0.remove(i);
                }
            }
        }

        if(dots0.size() == 0){
            nextStep = true;
        }
        System.out.println("Tutorial step: " + tutorialStep);
        System.out.println("Next step: " + nextStep);
        System.out.println("Dot0 size: " + dots0.size());




        if(dots0.size() > 0){
            nextStep = false;
        }

        if(nextStep && !showInstructions && tutorialStep <= 6){
//            GlyphLayout glyphLayout = new GlyphLayout();
//            glyphLayout.setText(font, "Tap to continue");
//            float textWidth = glyphLayout.width;
//            font.draw(batch, "Tap to continue", Constants.WIDTH/2 - textWidth/2, Constants.HEIGHT/2);
            tapToContinue.draw(batch);
        }else if (tutorialStep < 4){
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(font, instructionText);
            float textWidth = glyphLayout.width;
            font.draw(batch, instructionText, Constants.WIDTH/2 - textWidth/2, Constants.HEIGHT/2);
        }
        batch.end();

        stage.act();
        stage.draw();


        if(tutorialStep > 6){
            skipButton.setVisible(false);
            backButton.setVisible(false);
            homeButton.setVisible(true);
            replayTutButton.setVisible(true);
            playButton.setVisible(true);
            overlay.setVisible(true);
            String text = "Got it?";
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(hugeFont, text);
            float textWidth = glyphLayout.width;
            batch.begin();
            hugeFont.draw(batch, text, Constants.WIDTH/2 - textWidth/2, Constants.HEIGHT/2 + 310);
            batch.end();
        }


    }

    private void update(float delta) {
        // Update road position per frame
        roadYPos -= scrollSpeed * delta;
        if(roadYPos < -1536) {
            roadYPos = 0;
        }

        batch.begin();
        batch.end();

        if(moveLeftCar) {
            carLeft.getBoundingBox().x += delta * leftCarSpeed;
        }
        if(moveRightCar) {
            carRight.getBoundingBox().x += delta * rightCarSpeed;
        }
        snapCars();
    }


    @Override
    public void resize(int width, int height) {
        fitViewport.update(width,height,true);
        screenViewport.update(width,height,true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        solidLine.dispose();
        stripe.dispose();
        carRed.dispose();
        carBlue.dispose();
        dotRed.dispose();
        dotBlue.dispose();
        instructions.dispose();
        toStartMessage.getTexture().dispose();
        warning.getTexture().dispose();
        switchSprite.getTexture().dispose();
        switchBackSprite.getTexture().dispose();
        tapToContinue.getTexture().dispose();
        roadBg.dispose();
        stretchTexture.dispose();
    }


    private void populateMaps() {
        positionMap = new HashMap<Integer, Float>();
        positionMap.put(0, Constants.CAR_LEFT_P1);
        positionMap.put(1, Constants.CAR_LEFT_P2);
        positionMap.put(2, Constants.CAR_RIGHT_P1);
        positionMap.put(3, Constants.CAR_RIGHT_P2);

        dotPositionMap = new HashMap<Integer, Float>();
        dotPositionMap.put(0, Constants.DOT_LEFT_P1);
        dotPositionMap.put(1, Constants.DOT_LEFT_P2);
        dotPositionMap.put(2, Constants.DOT_RIGHT_P1);
        dotPositionMap.put(3, Constants.DOT_RIGHT_P2);
    }

    private void snapCars() {
        if(carRight.getBoundingBox().x > Constants.CAR_RIGHT_P2 - Constants.SNAPPING_DISTANCE && moveRightCar) {
            carRight.setPosition(Constants.CAR_RIGHT_P2, carRight.getYPosition());
            moveRightCar = false;
            carRight.getSprite().setRotation(0);
        }
        if(carRight.getBoundingBox().x < Constants.CAR_RIGHT_P1 + Constants.SNAPPING_DISTANCE && moveRightCar) {
            carRight.setPosition(Constants.CAR_RIGHT_P1, carRight.getYPosition());
            moveRightCar = false;
            carRight.getSprite().setRotation(0);
        }

        if(carLeft.getBoundingBox().x > Constants.CAR_LEFT_P2 - Constants.SNAPPING_DISTANCE && moveLeftCar) {
            carLeft.setPosition(Constants.CAR_LEFT_P2, carLeft.getYPosition());
            moveLeftCar = false;
            carLeft.getSprite().setRotation(0);
        }
        if(carLeft.getBoundingBox().x < Constants.CAR_LEFT_P1 + Constants.SNAPPING_DISTANCE && moveLeftCar) {
            carLeft.setPosition(Constants.CAR_LEFT_P1, carLeft.getYPosition());
            moveLeftCar = false;
            carLeft.getSprite().setRotation(0);
        }
    }

    private void loadTextures() {
        stretchTexture = new Texture("textures/carbon-fiber2.jpg");
        roadBg = new Texture("textures/bg.png");
        tapToContinue = new Sprite(new Texture("textures/taptocontinue.png"));
        switchSprite = new Sprite(new Texture("textures/switch.png"));
        switchBackSprite = new Sprite(new Texture("textures/switch_back.png"));
        warning = new Sprite(new Texture("textures/warning_icon4.png"));
        instructions = new Texture("textures/instructions5.png");
        toStartMessage = new Sprite(new Texture("textures/taptostart5.png"));
        solidLine = new Texture("textures/line.png");
        spriteSolid = new Sprite(solidLine);
        stripe = new Texture("textures/stripes.png");
        spriteStripe = new Sprite(stripe);
        carRed = new Texture("textures/car_red.png");
        carBlue = new Texture("textures/car_blue.png");
        dotRed = new Texture("textures/dot_red.png");
        dotBlue = new Texture("textures/dot_blue.png");
    }

    private void initViewports() {
        fitViewport = new FitViewport(1080,1920);
        screenViewport = new ScreenViewport();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = new Vector3(screenX,screenY, 0);
        fitViewport.getCamera().unproject(touchPos);

        System.out.println("x, y:" + touchPos.x + ", " + touchPos.y);
        if(showInstructions  && touchPos.y < 1700){
            showInstructions = false;
        }

        if(nextStep  && touchPos.y < 1700){
            String color = "blue";
            if(tutorialStep + 1 == 1 || tutorialStep + 1 == 2){
                color = "red";
            }

//        for (int i = 0; i < dots0.size(); i++) {
//            dots0.remove(i);
//        }

            dots0  = new ArrayList<Dot>();
            if(tutorialStep < 3){
                for (int i = 0; i < 2; i++) {
                    Dot d  = new Dot(new Vector2(dotPositionMap.get(tutorialStep + 1),1900 + i * distanceBetweenCars), color, color.equals("red") ? dotRed: dotBlue, 70);
                    dots0.add(d);
                }
            }
            if(tutorialStep == 4){
                for (int i = 0; i < 3; i++) {
                    Dot d1  = new Dot(new Vector2(dotPositionMap.get(0),1900 + i * distanceBetweenCars), "red", dotRed, 70);
                    Dot d2  = new Dot(new Vector2(dotPositionMap.get(1),2000 + i * distanceBetweenCars), "blue", dotBlue, 70);

                    dots0.add(d1);
                    dots0.add(d2);

                }
            }
            if(tutorialStep == 5){
                for (int i = 0; i < 3; i++) {

                    Dot d3  = new Dot(new Vector2(dotPositionMap.get(2),2100 + i * distanceBetweenCars), "blue", dotBlue, 70);
                    Dot d4  = new Dot(new Vector2(dotPositionMap.get(3),1900 + i * distanceBetweenCars), "red", dotRed, 70);

                    dots0.add(d3);
                    dots0.add(d4);
                }
            }
            tutorialStep++;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private void setButtonListeners() {
        homeButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(gsClient, handler));
                    }
                }, 0.10f);
                return true;
            }
        });

        playButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(gsClient, handler));
                    }
                }, 0.10f);
                return true;
            }
        });

        replayTutButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(gsClient, handler));
                    }
                }, 0.10f);
                return true;
            }
        });

        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(gsClient, handler));
                    }
                }, 0.10f);
                return true;
            }
        });

        skipButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen(gsClient, handler));
                    }
                }, 0.10f);
                return true;
            }
        });




    }
}