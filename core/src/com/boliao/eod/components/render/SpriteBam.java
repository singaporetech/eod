package com.boliao.eod.components.render;

import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 3/2/17.
 */

public class SpriteBam extends Sprite implements Renderable {
    public SpriteBam(String spritePath) {
        super("SpriteBam", spritePath, SETTINGS.BAM_SIZE, SETTINGS.BAM_SIZE);

        setAlpha(0);
    }

    public SpriteBam(String spritePath, float r, float g, float b, float a) {
        super("SpriteBam", spritePath, SETTINGS.BAM_SIZE, SETTINGS.BAM_SIZE, r, g, b, a);
    }

    @Override
    public void update(float dt) {
        // NO FRAME UPDATES, changes only by request
    }
}
