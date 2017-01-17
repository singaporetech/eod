package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 16/1/17.
 */

public class PlayScreen implements Screen {
    private final int CAMSPEED = 100;

    private boolean paused = false;
    private Texture img;

    // viewport
    private OrthographicCamera cam;
    private Viewport viewport;

    // game singletons
    Game game = Game.i();
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // game objects
    private com.boliao.eod.Hud hud;
    private TiledMap map;
    private TmxMapLoader mapLoader;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Human girl;

    // game objects list
    List<GameObject> gameObjects;

    /**
     * Ctor.
     */
    public PlayScreen () {
        // camera
        cam = new OrthographicCamera();
        viewport = new FitViewport(game.VIEWPORT_WIDTH, game.VIEWPORT_HEIGHT, cam);

        // init hud
        hud = new com.boliao.eod.Hud();

        // init game objects list
        gameObjects = new LinkedList<GameObject>();

        // init map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level0.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        // init human
        girl = new Human(100, 100, 30, "sprites/girl1.png");
        gameObjects.add(girl);
    }

    /**
     * The gameloop.
     * @param delta
     */
    @Override
    public void render(float delta) {
        // process inputs
        processInputs(delta);

        // process game state updates
        if (!paused) {
            update(delta);
        }

        // render
        draw();
    }

    private void processInputs(float delta) {

    }

    private void update (float delta) {
        hud.update();
        cam.update();
        mapRenderer.setView(cam);
        for (GameObject go: gameObjects) {
            go.update(delta);
        }
    }

    private void draw () {
        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw map
        mapRenderer.render();

        // draw hud
        //game.spriteBatch.setProjectionMatrix(cam.combined);
        hud.draw();

        // draw all game objects
        game.spriteBatch.begin();
        for (GameObject go: gameObjects) {
            go.draw();
        }
        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {

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

    }
}
