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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.boliao.eod.components.render.Renderable;

/**
 * Created by mrboliao on 16/1/17.
 */

public class Hud implements Renderable {

    private Texture img;
    private BitmapFont font;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParams;
    private GlyphLayout glyph;

    // view stuff
    private OrthographicCamera cam;
    private Viewport viewport;
    private Stage stage;

    // game singletons
    Game game = Game.i();
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // labels
    Label countdownLabel;
    Label stepsLabel;
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
        fontParams.size = 88;
        fontParams.color = Color.WHITE;
        fontParams.shadowOffsetX = 3;
        fontParams.shadowOffsetY = 3;
        fontParams.shadowColor = new Color(1, 1, 1, 0.5f);
        font = fontGenerator.generateFont(fontParams);
        //glyph = new GlyphLayout(font, gameState.steps+" steps");

        // create labels
        countdownLabel = new Label(String.format("%03d secs left", gameState.timer), new Label.LabelStyle(font, Color.WHITE));
        stepsLabel = new Label(String.format("%03d steps", gameState.steps), new Label.LabelStyle(font, Color.WHITE));

        // add labels to table
        table.add(countdownLabel).expandX().padTop(10);
        table.add(stepsLabel).expandX().padTop(10);
        table.row();

        stage.addActor(table);
    }

    public void update () {
        // set the text upon update of step count
        //glyph.setText(font, gameState.steps+" steps");
        countdownLabel.setText(String.format("%d secs left", gameState.timer));
        stepsLabel.setText(String.format("%d steps", gameState.steps));
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
