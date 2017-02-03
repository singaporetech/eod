package com.boliao.eod.components.render;

import com.badlogic.gdx.math.Rectangle;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.Component;

/**
 * Created by mrboliao on 3/2/17.
 */

public class SpriteBam extends Sprite implements Renderable {
    public SpriteBam(String spritePath) {
        super("SpriteBam", spritePath, SETTINGS.BAM_SIZE, SETTINGS.BAM_SIZE);

        setSpriteAlpha(0);
    }

    @Override
    public void update(float delta) {
        // NO FRAME UPDATES, changes only by request
    }
}
