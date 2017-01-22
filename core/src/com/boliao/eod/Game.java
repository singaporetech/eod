package com.boliao.eod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

public class Game extends com.badlogic.gdx.Game {
    private static Game instance = new Game();

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
