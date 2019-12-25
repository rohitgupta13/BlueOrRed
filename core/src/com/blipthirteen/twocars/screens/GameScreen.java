package com.blipthirteen.twocars.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.blipthirteen.twocars.misc.Constants;
import com.blipthirteen.twocars.objects.Car;
import com.blipthirteen.twocars.objects.Dot;

public class GameScreen implements Screen, InputProcessor{

	enum GameState{
		RUNNING,
		PAUSE,
		OVER
	}
	
	Preferences prefs;
	GameState currentState;
	
	// Scene 2D
	// Buttons
    ImageButton pauseButton, resumeButton, homeButton;
    Stage stage;
    Skin skin;
    TextureAtlas buttonatlas;
    Image overlay, window;
	
	private Map<Integer, Float> positionMap;
	private Map<Integer, Float> dotPositionMap;
	
	private List<Dot> obstaclesLeft;
	private List<Dot> obstaclesRight;
	
	private FitViewport fitViewport;
	private StretchViewport stretchViewport;
	private Texture carRed, carBlue;
	private Texture stripe, solidLine;
	private Texture dotRed, dotBlue;
	private Texture instructions;
	
	private Car carLeft, carRight;
	private SpriteBatch batch;
	
	// For reference
	private ShapeRenderer shapeRenderer;
	
	// Let's make the road scroll
	private float roadYPos, scrollSpeed;
	
	private boolean moveLeftCar, moveRightCar;
	private float rightCarSpeed, leftCarSpeed;
	
	private float lastDotLeft, lastDotRight;
	
	private boolean drawBigAssCircle;
	private String bigAssCircleColor;
	private Vector2 bigAssCirclePosition;
	private float growingRadius;
	private Color blue, red, gray, currentColor;
	
	private boolean gameOver;
	private boolean afterCircleHasBeenDrawn;
	
	private BitmapFont smallFont, font, hugeFont;
	private int score;
	
	private float showTime, switchTime;
	private String carLeftSafeColor;
	private String carLeftUnsafeColor;
	private String carRightSafeColor;
	private String carRightUnsafeColor;
	private boolean switching, showSwitchingText, showSwitchingBackText, shouldSwitch, shouldUnswitch;
	private boolean showInstructions;
	private Sprite spriteSolid, spriteStripe;
	
	// Gap in seconds
	private float switchGap = 10;
	private int randomMinimumScore;
	
	@Override
	public void show() {
		showInstructions = true;
		prefs = Gdx.app.getPreferences(Constants.PREF_NAME);
        currentState = GameState.RUNNING;
		showTime = 0;
		red = new Color((float)199/255, (float)60/255, (float)66/255, 1);
		blue = new Color((float)60/255, (float)66/255, (float)199/255, 1);
		gray = new Color((float)19/255, (float)19/255, (float)19/255, 1);
		currentColor = gray;
		growingRadius = 0.01f;
		roadYPos = 0;
		scrollSpeed = 510;
		lastDotLeft = Constants.FIRST_DOT;
		lastDotRight = Constants.FIRST_DOT;
		batch = new SpriteBatch();
		loadTextures();
		initViewports();
		populateMaps();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		generateFont();
		init();
		initScene2D();
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(stage);

		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	private void initScene2D(){
		stage = new Stage();
        stage.setViewport(fitViewport);
        skin= new Skin();
        buttonatlas = new TextureAtlas(Constants.TEXTURE_PACK);
        skin.addRegions(buttonatlas);
        pauseButton = new ImageButton(skin.getDrawable("pause_up"), skin.getDrawable("pause_down"));
        pauseButton.setPosition(80, Constants.HEIGHT - 82);
        
        resumeButton = new ImageButton(skin.getDrawable("resume_up"), skin.getDrawable("resume_down"));
        resumeButton.setPosition(Constants.WIDTH/2 - resumeButton.getWidth()/2 + 130, Constants.HEIGHT/2 - resumeButton.getHeight()/2);
        resumeButton.setVisible(false);
        
        homeButton = new ImageButton(skin.getDrawable("home_up"), skin.getDrawable("home_down"));
        homeButton.setPosition(Constants.WIDTH/2 - homeButton.getWidth()/2 - 130, Constants.HEIGHT/2 - homeButton.getHeight()/2);
        homeButton.setVisible(false);
        
        
        overlay = new Image(skin.getDrawable("cover"));
        overlay.setSize(Constants.WIDTH, Constants.HEIGHT);
        overlay.setVisible(false);
        
        window = new Image(skin.getDrawable("window"));
        window.setPosition(Constants.WIDTH/2 - window.getWidth()/2, Constants.HEIGHT/2 - window.getHeight()/2);
        window.setVisible(false);
        stage.addActor(overlay);
        stage.addActor(window);
        stage.addActor(pauseButton);
        stage.addActor(homeButton);
        stage.addActor(resumeButton);
        setButtonListeners();
	}
	
	private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 250;
        hugeFont = generator.generateFont(parameter);
        hugeFont.setColor(Color.WHITE);
        parameter.size = 60;
        font = generator.generateFont(parameter);
        font.setColor(Color.WHITE);
        parameter.size = 72;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.WHITE);
        generator.dispose();
    }
	
	private void init() {
		switching = false;
		showSwitchingText = false;
		showSwitchingBackText = false;
		shouldSwitch = true;
		shouldUnswitch = false;
		
		switchTime = 0;
		score = 0;
		randomMinimumScore = MathUtils.random(8, 13);
		
		carLeftSafeColor = "blue";
		carRightSafeColor = "red";
		carLeftUnsafeColor = "red";
		carRightUnsafeColor = "blue";
		
		obstaclesLeft = new ArrayList<Dot>();
		obstaclesRight = new ArrayList<Dot>();
		
		//
//		placeObstaclesLeft();
//		placeObstaclesRight();
		
		carLeft = new Car("carLeft", Constants.CAR_LEFT_P1, 80, carRed);
		carLeft.setSize(150, 279);
		carRight = new Car("carRight", Constants.CAR_RIGHT_P1, 80, carBlue);
		carRight.setSize(150, 279);
		gameOver = false;
		afterCircleHasBeenDrawn = false;
		
//		System.out.println("showSwitchingText :" + showSwitchingText + 
//				"\nshowSwitchingBackText :" + showSwitchingBackText +
//				"\nshouldSwitch :" + shouldSwitch + 
//				"\nshouldUnswitch :" + shouldUnswitch +
//				"\nrandomMinimumScore :" + randomMinimumScore 
//				);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(currentColor.r, currentColor.g, currentColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(stretchViewport.getCamera().combined);
        stretchViewport.apply(true);
        batch.begin();
        batch.end();
//        drawDebug();
        
		
		batch.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        batch.begin();
        
        // Draw a line on the left side
        spriteSolid.setPosition(40 - solidLine.getWidth()/2, 0);
//        sprite.setColor(Color.GREEN);
        spriteSolid.draw(batch);
        spriteSolid.setPosition(1080 - 40 - solidLine.getWidth()/2, 0);
        spriteSolid.draw(batch);
        //batch.draw(solidLine, 40 - solidLine.getWidth()/2, 0, solidLine.getWidth(), solidLine.getHeight());
        
//        batch.draw(solidLine, 40 - solidLine.getWidth()/2, 0, solidLine.getWidth(), solidLine.getHeight());
        // Draw a line on the right side
//        batch.draw(solidLine, 1080 - 40 - solidLine.getWidth()/2, 0, solidLine.getWidth(), solidLine.getHeight());
        
        // Striped lines through the centre
        
    	  spriteStripe.setPosition(540 - stripe.getWidth()/2, roadYPos);
    	  spriteStripe.draw(batch);
    	  spriteStripe.setPosition(540 - stripe.getWidth()/2, roadYPos + stripe.getHeight());
    	  spriteStripe.draw(batch);
    	  spriteStripe.setPosition(540 - stripe.getWidth()/2, roadYPos + stripe.getHeight() * 2);
    	  spriteStripe.draw(batch);
        
//        batch.draw(stripe, 540 - stripe.getWidth()/2, roadYPos, stripe.getWidth(), stripe.getHeight());
//        batch.draw(stripe, 540 - stripe.getWidth()/2, roadYPos + stripe.getHeight(), stripe.getWidth(), stripe.getHeight());
//        batch.draw(stripe, 540 - stripe.getWidth()/2, roadYPos + stripe.getHeight() * 2, stripe.getWidth(), stripe.getHeight());
        
        
		for (Dot dot : obstaclesLeft) {
			if (dot.isDraw()) {
				dot.draw(batch);
			}
		}

		for (Dot dot : obstaclesRight) {
			if (dot.isDraw()) {
				dot.draw(batch);
			}
		}
        
        carLeft.draw(batch);
        carRight.draw(batch);
        if(showInstructions) {
        	batch.draw(instructions, Constants.WIDTH/2 - instructions.getWidth()/2, Constants.HEIGHT/2 -200 - instructions.getHeight()/2);
        }
        	

        GlyphLayout glyphLayout = new GlyphLayout();
        String scoreText = "" + score;
        glyphLayout.setText(font, scoreText);
        float textWidth = glyphLayout.width;
        
        font.draw(batch, scoreText, Constants.WIDTH - 82 - textWidth, 1890);
        batch.end();

        
        stage.act();
        stage.draw();
        
		switch(currentState) {
		case RUNNING:
			update(delta);
			break;
		case PAUSE:
			break;
		case OVER:
			if(drawBigAssCircle) {
	        	drawBigAssCircle();
	        	if(!afterCircleHasBeenDrawn){
	        		growingRadius += Constants.GROWING_RADIUS_SPEED * delta;
	        	}
	        }
	        
	        if(growingRadius > Constants.HEIGHT + 300) {
	        	afterCircleHasBeenDrawn = true;
	        	batch.setProjectionMatrix(fitViewport.getCamera().combined);
	            fitViewport.apply(true);
	            batch.begin();

	            // For centering text
	            
	            String item = "" + score;
	            glyphLayout.setText(hugeFont,item);
	            float w = glyphLayout.width;
	            hugeFont.draw(batch, item, Constants.WIDTH/2 - w/2, 1140);

	            glyphLayout.setText(smallFont, Constants.GAMEOVER_MSG);
	            w = glyphLayout.width; 
	            smallFont.draw(batch, Constants.GAMEOVER_MSG, Constants.WIDTH/2 - w/2, 900);
	            batch.end();
	        	
//	        	Game game = (Game) Gdx.app.getApplicationListener();
//	        	game.setScreen(new GameScreen());
	        }
			break;
		}

	}
	
	private void update(float delta) {
		// Update road position per frame
        roadYPos -= scrollSpeed * delta;
        if(roadYPos < -1536) {
        	roadYPos = 0;
        }
        
        if(showSwitchingBackText || showSwitchingText) {
        	switching = true;
        }else {
        	switching = false;
        }
        
        if(!switching && switchTime > switchGap && score > randomMinimumScore) {
        	if(oneByFiveHundred()) {
            	if(shouldSwitch) {
            		System.out.println("Should be switching now");
            		shouldSwitch = false;
            		shouldUnswitch = true;
            		showSwitchingText = true;
            		switchColors();
            		clearObstacles();
            		placeObstaclesLeft();
            		placeObstaclesRight();
            		System.out.println("Switching after: " + switchTime);
            		switchTime = 0;
            	}
            }
        	else if(oneByFiveHundred()) {
            	if(shouldUnswitch) {
            		System.out.println("Should be switching back now");
            		shouldSwitch = true;
            		shouldUnswitch = false;
            		showSwitchingBackText = true;
            		clearObstacles();
            		switchColorsBack();
            		placeObstaclesLeft();
            		placeObstaclesRight();
            		System.out.println("Switching after: " + switchTime);
            		switchTime = 0;
            	}
            }
        }
        
        if(shouldSwitch || shouldUnswitch) {
        	switchTime += delta;
        }
        
		if (showTime > 2) {
			showSwitchingText = false;
			showSwitchingBackText = false;
			showTime = 0;
		}

		batch.begin();
		if (showSwitchingText) {
			font.draw(batch, "SWITCH!", 455, 960);
			showTime += delta;
		}

		if (showSwitchingBackText) {
			font.draw(batch, "SWITCH BACK!", 370, 960);
			showTime += delta;
		}
		batch.end();
		
		controls(delta);
		
		for (Dot dot : obstaclesLeft) {
			dot.getBoundingCircle().y -= delta * scrollSpeed;
		}

		for (Dot dot : obstaclesRight) {
			dot.getBoundingCircle().y -= delta * scrollSpeed;
		}
		
		
        if(moveLeftCar) {
    		carLeft.getBoundingBox().x += delta * leftCarSpeed;
        }
        if(moveRightCar) {
    		carRight.getBoundingBox().x += delta * rightCarSpeed;
        }
        
        if(!gameOver) {
            reGenerateLeft();
            reGenerateRight();
            // Game over?
            checkCollisions();
            checkMissed();
            updateSpeed();
        }
        snapCars();
	}

	private void switchColors() {
		carLeftSafeColor = "red";
		carRightSafeColor = "blue";
		carLeftUnsafeColor = "blue";
		carRightUnsafeColor = "red";
	}
	
	private void switchColorsBack() {
		carLeftSafeColor = "blue";
		carRightSafeColor = "red";
		carLeftUnsafeColor = "red";
		carRightUnsafeColor = "blue";
	}
	
	private void updateSpeed() {
		if(score > 8 && score < 16) {
			scrollSpeed = 570;
		}
		if(score >= 16 && score < 24) {
			scrollSpeed = 640;
		}
		if(score >= 24 && score < 32) {
			scrollSpeed = 710;
		}
		if(score >= 32 && score < 40) {
			scrollSpeed = 780;
		}
		if(score >= 40 && score <50) {
			scrollSpeed = 850;
		}
		if(score >= 50) {
			scrollSpeed = 900;
		}
	}
	
	@Override
	public void resize(int width, int height) {
		fitViewport.update(width,height,true);
		stretchViewport.update(width,height,true);
	}

	@Override
	public void pause() {
		pauseButton.setVisible(false);
		window.setVisible(true);
		overlay.setVisible(true);
		resumeButton.setVisible(true);
		homeButton.setVisible(true);
		currentState = GameState.PAUSE;
	}

	@Override
	public void resume() {
		pauseButton.setVisible(true);
		window.setVisible(false);
		overlay.setVisible(false);
		resumeButton.setVisible(false);
		homeButton.setVisible(false);
		currentState = GameState.RUNNING;
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		solidLine.dispose();
		stripe.dispose();
		carRed.dispose();
		carBlue.dispose();
		dotRed.dispose();
		dotBlue.dispose();
		instructions.dispose();
	}
	
	private void clearObstacles() {
		obstaclesLeft.clear();
		obstaclesRight.clear();
	}
	
	private void checkMissed() {
		for (Dot dot : obstaclesLeft) {
			if(dot.getYPosition() < -100 && dot.getColor().equals(carLeftSafeColor)) {
				if(dot.isDraw() && dot.isEnabled())
				{
					gameover(dot, "blue");
				}
			}
		}
		
		for (Dot dot : obstaclesRight) {
			if(dot.getYPosition() < -100 && dot.getColor().equals(carRightSafeColor)) {
				if(dot.isDraw() && dot.isEnabled())
				{
					gameover(dot, "red");
				}
			}
		}
	}
	
	
	private void checkCollisions() {
		for (Dot dot : obstaclesLeft) {
			if(carLeft.getBoundingBox().contains(dot.getBoundingCircle()) && dot.isDraw()) {
				if(dot.getColor().equals(carLeftUnsafeColor)) {
					gameover(dot, "blue");
				}else {
					if(dot.isDraw()) {
						score++;
						dot.setDraw(false);
					}
				}
			}
		}
		for (Dot dot : obstaclesRight) {
			if(carRight.getBoundingBox().contains(dot.getBoundingCircle()) && dot.isDraw()) {
				if(dot.getColor().equals(carRightUnsafeColor)) {
					gameover(dot, "red");
				}else {
					if(dot.isDraw()) {
						score++;
						dot.setDraw(false);
					}
				}
			}
		}
	}
	
	private void gameover(Dot dot, String color) {
		currentState = GameState.OVER;
		gameOver = true;
		bigAssCirclePosition = dot.getPosition();
		drawBigAssCircle = true;
		bigAssCircleColor = color;
		pauseButton.setVisible(false);
		resumeButton.setVisible(false);
		
		int highScore = prefs.getInteger("highScore");
		if(score > highScore) {
			prefs.putInteger("highScore", score);
			prefs.flush();
		}
	}
	
	private void drawBigAssCircle() {
		shapeRenderer.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeType.Filled);
        if(bigAssCircleColor.equals("red")) {
        	shapeRenderer.setColor(red);
        }else {
        	shapeRenderer.setColor(blue);
        }
        
        shapeRenderer.circle(bigAssCirclePosition.x, bigAssCirclePosition.y, growingRadius);
    	shapeRenderer.end();
	}
	
	private void drawDebug() {
		shapeRenderer.setProjectionMatrix(fitViewport.getCamera().combined);
        fitViewport.apply(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeType.Line);
        shapeRenderer.box(0, 0, 0, 1080, 1920, 0);
        carLeft.drawDebug(shapeRenderer);
        carRight.drawDebug(shapeRenderer);
        for(Dot dot : obstaclesLeft) {
			dot.drawDebug(shapeRenderer);
		}
        for(Dot dot : obstaclesRight) {
			dot.drawDebug(shapeRenderer);
		}
        shapeRenderer.end();
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
	
	private void placeObstaclesLeft() {
		lastDotLeft = Constants.FIRST_DOT;
		for (int i = 0; i < 10; i++) {
			float xPos = dotPositionMap.get(MathUtils.random(0, 1));
			String color = getRandomColor();
			Dot dot = new Dot(new Vector2(xPos, lastDotLeft), color, color.equals("red") ? dotRed: dotBlue, 70);
			obstaclesLeft.add(dot);
			float randomInt = MathUtils.random(1, 3);
			float randomFloat =  MathUtils.random(1f, randomInt);
			lastDotLeft += Constants.VERTICAL_DISTANCE_BW_DOTS * randomFloat;
		}
	}
	
	private void reGenerateLeft() {
		for (int i = 0; i < obstaclesLeft.size(); i++) { 
			if(obstaclesLeft.get(i).getYPosition() < -200) {
				float randomInt = MathUtils.random(1, 3);
				float randomFloat =  MathUtils.random(1f, randomInt);
				lastDotLeft = obstaclesLeft.get(obstaclesLeft.size() - 1).getYPosition() + Constants.VERTICAL_DISTANCE_BW_DOTS * randomFloat;
				int randomInteger = MathUtils.random(0, 1);
				float xPos = dotPositionMap.get(randomInteger);
				String color = getRandomColor();
				Dot dot = new Dot(new Vector2(xPos, lastDotLeft), color, color.equals("red") ? dotRed: dotBlue, 70);
				obstaclesLeft.add(dot);
				obstaclesLeft.remove(i);
			}
		}
	}
	
	private void placeObstaclesRight() {
		lastDotRight = Constants.FIRST_DOT;
		for (int i = 0; i < 10; i++) {
			float xPos = dotPositionMap.get(MathUtils.random(2, 3));
			String color = getRandomColor();
			Dot dot = new Dot(new Vector2(xPos, lastDotRight), color, color.equals("red") ? dotRed: dotBlue, 70);
			obstaclesRight.add(dot);
			float randomInt = MathUtils.random(1, 3);
			float randomFloat =  MathUtils.random(1f, randomInt);
			lastDotRight += Constants.VERTICAL_DISTANCE_BW_DOTS * randomFloat;
		}
	}
	
	private void reGenerateRight() {
		for (int i = 0; i < obstaclesRight.size(); i++) { 
			if(obstaclesRight.get(i).getYPosition() < -200) {
				float randomInt = MathUtils.random(1, 3);
				float randomFloat =  MathUtils.random(1f, randomInt);
				lastDotRight = obstaclesRight.get(obstaclesRight.size() - 1).getYPosition() + Constants.VERTICAL_DISTANCE_BW_DOTS * randomFloat;
				int randomInteger = MathUtils.random(2, 3);
				float xPos = dotPositionMap.get(randomInteger);
				String color = getRandomColor();
				Dot dot = new Dot(new Vector2(xPos, lastDotRight), color, color.equals("red") ? dotRed: dotBlue, 70);
				obstaclesRight.add(dot);
				obstaclesRight.remove(i);
			}
		}
	}
	
	private boolean oneByFiveHundred() {
		if(MathUtils.random(0, 500) == 233) {
			return true;
		} else {
			return false;
		}
	}
	
	private String getRandomColor() {
		int random = MathUtils.random(0, 1);
		if(random == 0) {
			return "red";
		}
		else {
			return "blue";
		}
	}
	
	private void controls(float delta) {
		if(Gdx.input.isKeyJustPressed(Keys.A) && !moveLeftCar) {
			if(carLeft.getXPosition() == Constants.CAR_LEFT_P1) {
				leftCarSpeed = Constants.CAR_SPEED_SIDEWAYS;
				moveLeftCar = true;
				carLeft.setTargetPositionX(Constants.CAR_LEFT_P2);
				carLeft.getSprite().setRotation(-10);
			}else {
				leftCarSpeed = -Constants.CAR_SPEED_SIDEWAYS;
				moveLeftCar = true;
				carLeft.setTargetPositionX(Constants.CAR_LEFT_P1);
				carLeft.getSprite().setRotation(10);
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.D) && !moveRightCar) {
			if(carRight.getXPosition() == Constants.CAR_RIGHT_P1) {
				rightCarSpeed = Constants.CAR_SPEED_SIDEWAYS;
				moveRightCar = true;
				carRight.setTargetPositionX(Constants.CAR_RIGHT_P2);
				carRight.getSprite().setRotation(-10);
			}else {
				rightCarSpeed = -Constants.CAR_SPEED_SIDEWAYS;
				moveRightCar = true;
				carRight.setTargetPositionX(Constants.CAR_RIGHT_P1);
				carRight.getSprite().setRotation(10);
			}
		}
			
		OrthographicCamera cam = (OrthographicCamera) fitViewport.getCamera();
        if(Gdx.input.isKeyPressed(Keys.NUM_1)) {
        	cam.zoom -= 1 * delta;
        }
        if(Gdx.input.isKeyPressed(Keys.NUM_2)) {
    		cam.zoom += 1 * delta;
        }
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
		instructions = new Texture("textures/instructions2.png");
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
		stretchViewport = new StretchViewport(1080,1920);
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
		if(showInstructions) {
			showInstructions = false;
			placeObstaclesLeft();
			placeObstaclesRight();
		}
		
		if(afterCircleHasBeenDrawn) {
        	Game game = (Game) Gdx.app.getApplicationListener();
        	game.setScreen(new GameScreen());
		}
		if(currentState == GameState.RUNNING) {
			Vector3 touchPos = new Vector3(screenX,screenY, 0);
			stretchViewport.getCamera().unproject(touchPos);
			
			if(touchPos.y < 1800 && touchPos.x < Constants.HORIZONTAL_CENTRE && !moveLeftCar) {
				if(carLeft.getXPosition() == Constants.CAR_LEFT_P1) {
					leftCarSpeed = Constants.CAR_SPEED_SIDEWAYS;
					moveLeftCar = true;
					carLeft.setTargetPositionX(Constants.CAR_LEFT_P2);
					carLeft.getSprite().setRotation(-10);
				}else {
					leftCarSpeed = -Constants.CAR_SPEED_SIDEWAYS;
					moveLeftCar = true;
					carLeft.setTargetPositionX(Constants.CAR_LEFT_P1);
					carLeft.getSprite().setRotation(10);
				}
			}
			
			if(touchPos.x > Constants.HORIZONTAL_CENTRE && !moveRightCar) {
				if(carRight.getXPosition() == Constants.CAR_RIGHT_P1) {
					rightCarSpeed = Constants.CAR_SPEED_SIDEWAYS;
					moveRightCar = true;
					carRight.setTargetPositionX(Constants.CAR_RIGHT_P2);
					carRight.getSprite().setRotation(-10);
				}else {
					rightCarSpeed = -Constants.CAR_SPEED_SIDEWAYS;
					moveRightCar = true;
					carRight.setTargetPositionX(Constants.CAR_RIGHT_P1);
					carRight.getSprite().setRotation(10);
				}
			}
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
	public boolean scrolled(int amount) {
		return false;
	}
	
	private void setButtonListeners() {
        pauseButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							pause();
                    }
                }, 0.10f);

                return true;
            }
        });
        
        resumeButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
							resume();
                    }
                }, 0.10f);

                return true;
            }
        });
        
        homeButton.addListener(new InputListener(){
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