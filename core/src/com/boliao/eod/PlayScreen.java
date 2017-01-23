package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.boliao.eod.components.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 16/1/17.
 */

public class PlayScreen implements Screen {
    private final int CAMSPEED = 100;

    private boolean paused = false;

    // viewport
    private OrthographicCamera cam;
    private Viewport viewport;

    // game singletons
    Game game = Game.i();
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // game objects
    private com.boliao.eod.Hud hud;
//    private TiledMap map;
//    private TmxMapLoader mapLoader;
//    private OrthogonalTiledMapRenderer mapRenderer;

    // game objects list
    List<GameObject> gameObjects;

    // Renderables list
    List<com.boliao.eod.components.Renderable> renderables;

    /**
     * Ctor.
     */
    public PlayScreen () {
        // camera
        cam = new OrthographicCamera();
        RenderEngine.i().setCam(cam);
        viewport = new FitViewport(SETTINGS.VIEWPORT_WIDTH, SETTINGS.VIEWPORT_HEIGHT, cam);
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        // init hud
        hud = new com.boliao.eod.Hud();

        // init game objects list
        gameObjects = new LinkedList<GameObject>();

        // init house
        GameObject house = new GameObject("house");
        gameObjects.add(house);
        house.addComponent(new Transform(SETTINGS.HOUSE_POS_X, SETTINGS.HOUSE_POS_Y, 0));
        house.addComponent(new Sprite("sprites/house.png", SETTINGS.HOUSE_SIZE));
        house.init();

        // init block
        GameObject block = new GameObject("block");
        gameObjects.add(block);
        block.addComponent(new Transform(SETTINGS.BLOCK_POS_X, SETTINGS.BLOCK_POS_Y, 0));
        block.addComponent(new Sprite("sprites/block.png", SETTINGS.BLOCK_SIZE));
        block.addComponent(new Collider());
        block.init();
//        mapLoader = new TmxMapLoader();
//        map = mapLoader.load("level0.tmx");
//        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // init human
        GameObject player = new GameObject("girl");
        gameObjects.add(player);
        player.addComponent(new Transform(SETTINGS.PLAYER_POS_X, SETTINGS.PLAYER_POS_Y, 0));
        player.addComponent(new SpriteSheet("sprites/player.txt"));
        player.addComponent(new Movement());
        player.addComponent(new SteeringArrive());
        player.addComponent(new FsmPlayer());
        player.addComponent(new Input(Input.InputType.TOUCH));
        player.init();

        // test init bug
        GameObject bug = new GameObject("bug");
        gameObjects.add(bug);
        bug.addComponent(new Transform(SETTINGS.BUG_POS_X, SETTINGS.BUG_POS_Y, 50));
        bug.addComponent(new SpriteSheet("sprites/bug1.txt"));
        bug.addComponent(new Movement(SETTINGS.SPEED_BUG));
        //bug.addComponent(new SteeringPursue(player)); //
        bug.addComponent(new SteeringPursueCollision(player)); //todo: anyway to auto extract name into init
        bug.addComponent(new FsmBug());
        bug.init();
    }

    /**
     * The gameloop.
     * @param delta
     */
    @Override
    public void render(float delta) {
        // process game state updates
        if (!paused) {
            update(delta);
        }

        // render
        draw();
    }

    private void update (float delta) {
        hud.update();
        cam.update();
        //mapRenderer.setView(cam);
        for (GameObject go: gameObjects) {
            go.update(delta);
        }
    }

    private void draw () {
        // clear screen
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw map
        //mapRenderer.render();

        // draw hud
        //game.spriteBatch.setProjectionMatrix(cam.combined);
        hud.draw();

        // draw all game objects
        game.spriteBatch.begin();
            RenderEngine.i().tick();
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
        for (GameObject go: gameObjects) {
            go.finalize();
        }
    }
}
