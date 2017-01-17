package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by mrboliao on 17/1/17.
 */

public class Human extends GameObject {
    public static final String TAG = "PlayScreen";
    private final int SPEED = GameState.i().HUMAN_SPEED;

    public Human (int x, int y, int r, String spritePath) {
        super(x, y, r, spritePath);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isTouched()) {
            move(SPEED*delta, 0);
            Gdx.app.log(TAG, "touched");
        }
    }

    private void move (float x, float y) {
        pos.add(x, y);
        sprite.translate(x, y);
    }
}
