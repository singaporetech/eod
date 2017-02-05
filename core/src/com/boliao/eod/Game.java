package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

import java.util.Calendar;
import java.util.Date;

public class Game extends com.badlogic.gdx.Game {
    private static final String TAG = "Game";

    private boolean canIncScore = false;

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

    public void restart() {
        RenderEngine.i().shutdownDebugRenderer();
        RenderEngine.i().clearRenderables();
        CollisionEngine.i().clearCollidables();

        Game.i().resume();
        playScreen.restart();
        RenderEngine.i().initDebugRenderer();
        RenderEngine.i().hideEndGameMenu();
    }

    public void keepScore() {
        if (gameState.getTimer() == 0) {
            canIncScore = true;
        }
        if (canIncScore) {
            gameState.incNumNights();
            Gdx.app.log(TAG, "+1 POINT!");
            canIncScore = false;
        }
    }

    @Override
    public void render() {
        super.render();

        keepScore();
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
        playScreen.pause();
    }

    @Override
    public void resume() {
        super.resume();
        playScreen.resume();
    }
}
