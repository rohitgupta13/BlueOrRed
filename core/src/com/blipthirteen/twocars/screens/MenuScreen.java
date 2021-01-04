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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.blipthirteen.twocars.adhandler.AdHandler;
import com.blipthirteen.twocars.misc.Constants;
import com.blipthirteen.twocars.toast.Toast;
import com.blipthirteen.twocars.tween.ImageAccessor;
import com.blipthirteen.twocars.tween.ImageButtonAccessor;
import com.blipthirteen.twocars.tween.ImageButtonAccessor2;
import com.blipthirteen.twocars.tween.SpriteAccessor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

/**
 * Created by rohit on 25-12-2016.
 */
public class MenuScreen implements Screen{

    private final List<Toast> toasts = new LinkedList<Toast>();
    private Toast.ToastFactory toastFactory;
    private BitmapFont smallFont;

    // Tween
    TweenManager tm;

    // Buttons
    ImageButton playButton, hsButton, helpButton, moreGamesButton, achsButton;
    ImageButton musicButton, soundButton;
    ImageButton.ImageButtonStyle musicButtonStyle, soundButtonStyle;


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

    private IGameServiceClient gsClient;
    Preferences prefs;
    private AdHandler handler;

    public MenuScreen(IGameServiceClient gsClient, AdHandler handler){
        this.gsClient = gsClient;
        this.handler = handler;
    }

    public void show() {
        handler.showAds(false);
        prefs = Gdx.app.getPreferences("com.blipthirteen.twocars");
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
        hsButton = new ImageButton(skin.getDrawable("leaderboard_up"),skin.getDrawable("leaderboard_down"));
        helpButton = new ImageButton(skin.getDrawable("howtoplay_up"),skin.getDrawable("howtoplay_down"));
        moreGamesButton = new ImageButton(skin.getDrawable("moregames_up"),skin.getDrawable("moregames_down"));
        achsButton = new ImageButton(skin.getDrawable("achievements_up"),skin.getDrawable("achievements_down"));

        musicButtonStyle = new ImageButton.ImageButtonStyle();
        musicButtonStyle.up = skin.getDrawable("music");
        musicButtonStyle.checked = skin.getDrawable("nomusic");
        musicButton = new ImageButton(musicButtonStyle);

        soundButtonStyle = new ImageButton.ImageButtonStyle();
        soundButtonStyle.up = skin.getDrawable("audio");
        soundButtonStyle.checked = skin.getDrawable("noaudio");
        soundButton = new ImageButton(soundButtonStyle);

        musicButton.setChecked(!prefs.getBoolean("music"));
        soundButton.setChecked(!prefs.getBoolean("sound"));

        image = new Image(skin.getDrawable("twocars"));

        setButtonPostion();
        Gdx.input.setInputProcessor(stage);

        stage.addActor(image);
        stage.addActor(playButton);
        stage.addActor(hsButton);
        stage.addActor(helpButton);
        stage.addActor(moreGamesButton);
        stage.addActor(soundButton);
        stage.addActor(musicButton);
        stage.addActor(achsButton);

        generateFont();
        toastFactory = new Toast.ToastFactory.Builder()
                .font(smallFont)
                .build();

//        toastFactory = new Toast.ToastFactory.Builder()
//                .font(smallFont)
//                .backgroundColor(new Color(0.5f, 0.5f, 0.5f, 1f))
//                .fadingDuration(1.2f)
//                .fontColor(new Color(1f, 0.2f, 0.68f, 0.75f))
//                .margin(20)
//                .maxTextRelativeWidth(0.5f)
//                .positionY(100)
//                .build();

        t = new Timer();
        t.scheduleTask(new Task() {
            @Override
            public void run() {
                setButtonListeners();
            }
        }, .25f);

        tm = new TweenManager();
        Tween.registerAccessor(ImageButton.class,new ImageButtonAccessor());
        Tween.registerAccessor(Sprite.class,new SpriteAccessor());

        Tween.set(redStrip, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(redStrip, SpriteAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(blueStrip, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(blueStrip, SpriteAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(redCar, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(redCar, SpriteAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(blueCar, SpriteAccessor.ALPHA).target(0).start(tm);
        Tween.to(blueCar, SpriteAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(playButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(playButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(hsButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(hsButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(helpButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(helpButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(moreGamesButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(moreGamesButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(achsButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(achsButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(musicButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(musicButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.set(soundButton, ImageButtonAccessor.ALPHA).target(0).start(tm);
        Tween.to(soundButton, ImageButtonAccessor.ALPHA, 1.5f).target(1).start(tm);



        Tween.registerAccessor(Image.class,new ImageAccessor());
        Tween.set(image, ImageAccessor.ALPHA).target(0).start(tm);
        Tween.to(image, ImageAccessor.ALPHA, 1.5f).target(1).start(tm);

        Tween.registerAccessor(ImageButton.class,new ImageButtonAccessor2());
        Tween.to(playButton, ImageButtonAccessor2.SCALE_XY, .85f).target(.85f,.85f).repeatYoyo(-1,0).start(tm);
        playButton.setOrigin(Align.center);
        playButton.setTransform(true);


        tm.update(Float.MIN_VALUE); // update once avoid short flash of splash before animation

        gsClient.logIn();
    }

    private void generateFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        smallFont = generator.generateFont(parameter);
        smallFont.setColor(Color.WHITE);
        generator.dispose();
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


        stage.act();
        stage.draw();

        batch.begin();
        // handle toast queue and display
        Iterator<Toast> it = toasts.iterator();
        while(it.hasNext()) {
            Toast t = it.next();
            if (!t.render(Gdx.graphics.getDeltaTime())) {
                it.remove(); // toast finished -> remove
            } else {
                break; // first toast still active, break the loop
            }
        }
        batch.end();

        tm.update(delta);

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

        musicButton.setPosition(Constants.WIDTH/2 - helpButton.getWidth()/2 - 250, 180);
        soundButton.setPosition(Constants.WIDTH/2 - hsButton.getWidth()/2 - 380, 300);


        helpButton.setPosition(Constants.WIDTH/2 - helpButton.getWidth()/2 - 100, 90);
        moreGamesButton.setPosition(Constants.WIDTH/2 - moreGamesButton.getWidth()/2 + 100, 90);

        achsButton.setPosition(Constants.WIDTH/2 - helpButton.getWidth()/2 + 250, 180);
        hsButton.setPosition(Constants.WIDTH/2 - hsButton.getWidth()/2 + 380, 300);
    }

    private void setButtonListeners() {

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

        hsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        //((Game)Gdx.app.getApplicationListener()).setScreen(new HighScoreScreen());
                        try {
                            if(!gsClient.isSessionActive()){
                                gsClient.logIn();
                            }
                            gsClient.showLeaderboards(Constants.LEADERBOARD_ID);
                        } catch (GameServiceException e) {
                            //toasts.add(toastFactory.create("Play Services Error", Toast.Length.LONG));
                            e.printStackTrace();
                        }
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
                        ((Game)Gdx.app.getApplicationListener()).setScreen(new TutorialScreen(gsClient, handler));
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

        soundButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean b = prefs.getBoolean("sound");
                prefs.putBoolean("sound",!b);
                prefs.flush();
                return true;
            }
        });

        musicButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                boolean b = prefs.getBoolean("music");
                prefs.putBoolean("music",!b);
                prefs.flush();
                return true;
            }
        });

        achsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Timer.schedule(new Task(){
                    @Override
                    public void run() {
                        try {
                            if(!gsClient.isSessionActive()){
                                gsClient.logIn();
                            }
                            gsClient.showAchievements();
                        } catch (GameServiceException e) {
                            //toasts.add(toastFactory.create(e.getMessage(), Toast.Length.LONG));
                            e.printStackTrace();
                        }
                    }
                }, 0.10f);

                return true;
            }
        });
    }
}