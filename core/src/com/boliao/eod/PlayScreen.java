package com.boliao.eod;

import com.badlogic.gdx.Screen;
import com.boliao.eod.components.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 16/1/17.
 */

public class PlayScreen implements Screen {
    private final int CAMSPEED = 100;

    private boolean paused = false;

    // game singletons
    Game game = Game.i();
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // game objects list
    List<GameObject> gameObjects;

    // Renderables list
    List<com.boliao.eod.components.Renderable> renderables;

    // Tiled stuff
//    private TiledMap map;
//    private TmxMapLoader mapLoader;
//    private OrthogonalTiledMapRenderer mapRenderer;

    /**
     * Ctor.
     */
    public PlayScreen () {
        // init render engine
        RenderEngine.i().init();

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

        // init human
        GameObject player = new GameObject("player");
        gameObjects.add(player);
        player.addComponent(new Transform(SETTINGS.PLAYER_POS_X, SETTINGS.PLAYER_POS_Y, 0));
        player.addComponent(new SpriteSheet("sprites/player.txt"));
        player.addComponent(new Collider(SETTINGS.COLLISION_WIDTH,SETTINGS.COLLISION_HEIGHT));
        player.addComponent(new Movement());
        //player.addComponent(new SteeringArrive());
        player.addComponent(new SteeringArriveCollision());
        player.addComponent(new FsmPlayer());
        player.addComponent(new Input(Input.InputType.TOUCH));
        player.init();

        // test init bug
//        GameObject bug = new GameObject("bug");
//        gameObjects.add(bug);
//        bug.addComponent(new Transform(SETTINGS.BUG_POS_X, SETTINGS.BUG_POS_Y, 50));
//        bug.addComponent(new SpriteSheet("sprites/bug1.txt"));
//        bug.addComponent(new Movement(SETTINGS.SPEED_BUG));
//        bug.addComponent(new Collider());
//        //bug.addComponent(new SteeringPursue(player)); //
//        bug.addComponent(new SteeringPursueCollision(player)); //todo: anyway to auto extract name into init
//        bug.addComponent(new FsmBug());
//        bug.init();

//        mapLoader = new TmxMapLoader();
//        map = mapLoader.load("level0.tmx");
//        mapRenderer = new OrthogonalTiledMapRenderer(map);
    }

    /**
     * The gameloop.
     * @param delta
     */
    @Override
    public void render(float delta) {
        if (!paused) {
            // process game object updates
            for (GameObject go: gameObjects) {
                go.update(delta);
            }

            // process collisions
            CollisionEngine.i().tick();
        }

        // process graphics
        RenderEngine.i().tick();
    }

    @Override
    public void resize(int width, int height) {
        RenderEngine.i().setViewport(width, height);
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

        //shut down engine
        RenderEngine.i().finalize();
    }
}
