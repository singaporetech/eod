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
    Date today;

	@Override
	public void create () {
        // init engines
        RenderEngine.i().init();
        CollisionEngine.i().init();

        playScreen = new PlayScreen();
        setScreen(playScreen);

        // decrease timer every sec
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                gameState.decTimer();
            }
        }, 1, 1);
    }

    public List<GameObject> getGameObjects() {
        return playScreen.getGameObjects();
    }

    public void restart() {
        RenderEngine.i().shutdownDebugRenderer();
        RenderEngine.i().clearRenderables();
        CollisionEngine.i().clearCollidables();
        GameState.i().reset();

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
