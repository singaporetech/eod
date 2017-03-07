package com.boliao.eod;

import com.badlogic.gdx.Screen;
import com.boliao.eod.components.Combat;
import com.boliao.eod.components.Health;
import com.boliao.eod.components.Input;
import com.boliao.eod.components.Movement;
import com.boliao.eod.components.SpawnMgr;
import com.boliao.eod.components.Transform;
import com.boliao.eod.components.ai.FsmBug;
import com.boliao.eod.components.ai.FsmPlayer;
import com.boliao.eod.components.ai.SteeringArrive;
import com.boliao.eod.components.ai.SteeringPursue;
import com.boliao.eod.components.collision.Collider;
import com.boliao.eod.components.render.PrimitiveHealthPlayer;
import com.boliao.eod.components.render.SpriteHealth;
import com.boliao.eod.components.render.Sprite;
import com.boliao.eod.components.render.SpriteBam;
import com.boliao.eod.components.render.SpriteHealthPlayer;
import com.boliao.eod.components.render.SpriteInput;
import com.boliao.eod.components.render.SpritePlusOne;
import com.boliao.eod.components.render.SpriteSheetBug;
import com.boliao.eod.components.render.SpriteSheetPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 16/1/17.
 */

public class PlayScreen implements Screen {
    private boolean paused = false;

    // game singletons
    com.boliao.eod.GameState gameState = com.boliao.eod.GameState.i();

    // game objects list
    protected List<GameObject> gameObjects;

    /**
     * Ctor.
     */
    public PlayScreen () {
        // init game objects list
        gameObjects = new LinkedList<GameObject>();

        init();
    }

    public void init() {

        // init block
        GameObject block = new GameObject("block1");
        gameObjects.add(block);
        block.addComponent(new Transform(SETTINGS.BLOCK_POS_X, SETTINGS.BLOCK_POS_Y, 0));
        block.addComponent(new Sprite("sprites/block.png", SETTINGS.BLOCK_SIZE));
        block.addComponent(new Collider());
        block.init();

        // init block
        block = new GameObject("block2");
        gameObjects.add(block);
        block.addComponent(new Transform(SETTINGS.BLOCK_POS_X+200, SETTINGS.BLOCK_POS_Y+300, 0));
        block.addComponent(new Sprite("sprites/block.png", 80));
        block.addComponent(new Collider());
        block.init();

        // init block
        block = new GameObject("block3");
        gameObjects.add(block);
        block.addComponent(new Transform(SETTINGS.BLOCK_POS_X-200, SETTINGS.BLOCK_POS_Y+500, 0));
        block.addComponent(new Sprite("sprites/block.png", 100));
        block.addComponent(new Collider());
        block.init();

        /**
         * Overview: Game Engines
         * 1. The entity-component system.
         *
         * Physics: collisions
         * 1. The collider component.
         */
        // init human
        GameObject player = new GameObject("player");
        gameObjects.add(player);
        player.addComponent(new Transform(SETTINGS.PLAYER_POS_X, SETTINGS.PLAYER_POS_Y, 0));
        player.addComponent(new SpriteSheetPlayer("sprites/player.txt"));
        player.addComponent(new Collider(false, false));
        player.addComponent(new Movement());
        player.addComponent(new SteeringArrive());
        player.addComponent(new SteeringPursue(null));
        player.addComponent(new FsmPlayer());
        player.addComponent(new SpriteInput("sprites/x.png"));
        player.addComponent(new Input(Input.InputType.TOUCH));
        player.addComponent(new SpritePlusOne("sprites/plus1.png"));
        player.addComponent(new Health(0.5f));
        player.addComponent(new PrimitiveHealthPlayer());
        player.addComponent(new SpriteBam("sprites/bam.png"));
        player.addComponent(new Combat(null, SETTINGS.PLAYER_DMG));
        player.init();

        // give player handle to gameState so that sensors can be linked to player stats
        gameState.setPlayerHealth(player);

        // init spawn manager
        GameObject spawnMgr = new GameObject("SpawnMgr");
        gameObjects.add(spawnMgr);
        spawnMgr.addComponent(new SpawnMgr(player));
        spawnMgr.init();
    }

    public void restart() {
        dispose();

        init();
    }

    /**
     * 1. The gameloop.
     * @param dt
     */
    private void gameLoop(float dt) {
        if (!paused) {
            // process game object updates
            for (GameObject go: gameObjects) {
                go.update(dt);
            }

            // process collisions
            CollisionEngine.i().tick();
        }

        // process graphics
        RenderEngine.i().tick();
    }

    @Override
    public void render(float dt) {
        gameLoop(dt);
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
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        for (GameObject go: gameObjects) {
            go.finalize();
        }
        gameObjects.clear();
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}
