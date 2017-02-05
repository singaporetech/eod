package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.boliao.eod.components.Input;
import com.boliao.eod.components.render.Renderable;

/**
 * Created by mrboliao on 16/1/17.
 */

public class Hud implements Renderable {
    private static final String TAG = "Hud";

    private BitmapFont font;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParams;

    // view stuff
    private OrthographicCamera cam;
    private Viewport viewport;
    private Stage stage;

    // game singletons
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // UI elements
    Label countdownLabel;
    Label stepsLabel;
    Label scoreLabel;
    Label gameOverLabel;
    Button restartButton;
    TextButton.TextButtonStyle textButtonStyle;

    /**
     * Ctor
     */
    public Hud () {
        // init cam, viewport and stage
        cam = new OrthographicCamera();
        viewport = new FitViewport(SETTINGS.VIEWPORT_WIDTH, SETTINGS.VIEWPORT_HEIGHT, cam);
        stage = new Stage(viewport, RenderEngine.i().getSpriteBatch());

        // create table
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // create text to display on screen
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel.ttf"));
        fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 80;
        fontParams.color = Color.WHITE;
        fontParams.shadowOffsetX = 3;
        fontParams.shadowOffsetY = 3;
        fontParams.shadowColor = new Color(1, 1, 1, 0.5f);
        font = fontGenerator.generateFont(fontParams);
        //glyph = new GlyphLayout(font, gameState.steps+" steps");

        // button style
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.CYAN;
        textButtonStyle.overFontColor = Color.RED;

        Color lightGreen = new Color(0.85f, 1, 0.85f, 1);

        // create labels
        countdownLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        stepsLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        gameOverLabel = new Label("sorry UP LORRY oredi\ngo exercise more and", new Label.LabelStyle(font, lightGreen));
        gameOverLabel.setAlignment(Align.center);
        scoreLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));

        // create buttons
        Gdx.input.setInputProcessor(stage);
        restartButton = new TextButton("TRY AGAIN", textButtonStyle);
        restartButton.addListener( new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Gdx.app.log(TAG, "RESTART TOUCHUP!");
                Game.i().restart();
            }
        });

        // add labels to table
        table.add(scoreLabel).expandX().align(Align.left).padTop(10);
        table.add(stepsLabel).expandX().align(Align.right).padTop(10);
        table.row();
        table.add(gameOverLabel).align(Align.center).expandX().colspan(2).padTop(350);
        table.row();
        table.add(restartButton).center().expandX().colspan(2).padTop(20);
        table.row();
        table.add(countdownLabel).colspan(2).expandY().align(Align.bottom).padBottom(10);

        stage.addActor(table);

        // make game over stuff invisible first
        hideEndGameMenu();
    }

    public void showEndGameMenu() {
        gameOverLabel.setVisible(true);
        restartButton.setVisible(true);
    }

    public void hideEndGameMenu() {
        gameOverLabel.setVisible(false);
        restartButton.setVisible(false);
    }

    public void update () {
        // set the text upon update of step count
        //glyph.setText(font, gameState.steps+" steps");
        countdownLabel.setText(String.format("%03ds to OuTBReaK", gameState.getTimer()));

        if (gameState.getSteps() == 1) {
            stepsLabel.setText(String.format("1 step taken"));
        }
        else {
            stepsLabel.setText(String.format("%d steps taken", gameState.getSteps()));
        }

        if (gameState.getNumNights() == 0) {
            scoreLabel.setText(String.format("1st day..."));
        }
        else {
            scoreLabel.setText(String.format("%d nights", gameState.getNumNights()));
        }
    }

    public Camera getStageCam() {
        return stage.getCamera();
    }

    @Override
    public void draw () {
        stage.draw();
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }
}
