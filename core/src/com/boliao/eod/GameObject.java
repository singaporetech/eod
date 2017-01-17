package com.boliao.eod;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by mrboliao on 17/1/17.
 */

public abstract class GameObject {
    protected Vector2 pos;
    protected int rot;
    protected Sprite sprite;

    public GameObject (int x, int y, int r, String spritePath) {
        pos = new Vector2(x, y);
        rot = r;
        sprite = new Sprite(new Texture(spritePath));
        sprite.setX(x);
        sprite.setY(y);
    }

    public abstract void update(float delta);

    public void draw() {
        sprite.draw(Game.i().spriteBatch);
    }
}
