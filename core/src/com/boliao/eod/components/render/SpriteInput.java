package com.boliao.eod.components.render;

import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 3/2/17.
 */

public class SpriteInput extends Sprite implements RenderableDebug {
    public SpriteInput(String spritePath) {
        super("SpriteInput", spritePath, SETTINGS.X_SIZE, SETTINGS.X_SIZE);
    }

    @Override
    public void update(float delta) {
        // NO FRAME UPDATES, changes only by request
    }
}
