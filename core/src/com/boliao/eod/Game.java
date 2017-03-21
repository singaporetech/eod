package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Game extends com.badlogic.gdx.Game {
    private static final String TAG = "Game";

    private static Game instance = new Game();
    private Game() {}
    public static Game i(){
        return instance;
    }

    private PlayScreen playScreen;
    private GameState gameState = GameState.i();

	@Override
	public void create () {
        // init engines
        RenderEngine.i().init();
        CollisionEngine.i().init();

        playScreen = new PlayScreen();
        setScreen(playScreen);

        restart();
    }

    public List<GameObject> getGameObjects() {
        return playScreen.getGameObjects();
    }

    public void restart() {
        RenderEngine.i().shutdownDebugRenderer();
        RenderEngine.i().clearRenderables();
        CollisionEngine.i().clearCollidables();

        //TODO shift this elsewhere to allow reset when gameplay resets
        //GameState.i().reset();

        Game.i().resume();
        playScreen.restart();
        RenderEngine.i().initDebugRenderer();
        RenderEngine.i().hideEndGameMenu();
    }

    @Override
    public void render() {
        super.render();
    }

	@Override
	public void dispose () {
        Gdx.app.log(TAG, "in Game.dispose()");
        playScreen.dispose();

        CollisionEngine.i().finalize();
        RenderEngine.i().finalize();
	}

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }
}
