package com.boliao.eod.components.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 3/2/17.
 */

public class SpriteHealth extends Sprite implements Renderable {
    protected float maxWidth = SETTINGS.HEALTHBAR_WIDTH;
    protected float width = maxWidth;

    public SpriteHealth(String spritePath) {
        super("SpriteHealth", spritePath, SETTINGS.HEALTHBAR_WIDTH, SETTINGS.HEALTHBAR_HEIGHT);
        sprite.setOrigin(0,0);
    }

    @Override
    public void update(float dt) {
        sprite.setCenter(transform.getX(), transform.getY() + SETTINGS.HEALTHBAR_OFFSET_Y);
    }

    public void scaleWidth(float scale) {
        width = scale * maxWidth;
        if (width < 0) {
            width = 0;
        }

        sprite.setScale(scale, 1);
    }
}
