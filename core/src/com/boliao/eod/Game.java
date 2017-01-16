package com.boliao.eod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

public class Game extends com.badlogic.gdx.Game {
    private static Game instance = new Game();

    public static final int VIEWPORT_WIDTH = 720;
    public static final int VIEWPORT_HEIGHT = 1280;
	public SpriteBatch spriteBatch;
    //public GameState gameState;

    private Game() {}

    public static Game i(){
        return instance;
    }

	@Override
	public void create () {
        spriteBatch = new SpriteBatch();
        setScreen(new PlayScreen());
        //gameState = GameState.i();

        // decrease timer every sec
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                --GameState.i().timer;
            }
        }, 1, 1);
    }

    @Override
    public void render() {
        super.render();
    }

	@Override
	public void dispose () {
		spriteBatch.dispose();
	}
}
