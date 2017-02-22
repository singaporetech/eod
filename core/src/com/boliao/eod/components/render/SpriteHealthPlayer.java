package com.boliao.eod.components.render;

import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 6/2/17.
 */

public class SpriteHealthPlayer extends SpriteHealth {

    public SpriteHealthPlayer(String spritePath) {
        super(spritePath);

        // set to fixed top of screen
        sprite.setCenter(SETTINGS.HEALTHBAR_PLAYER_X, SETTINGS.HEALTHBAR_PLAYER_Y);
        sprite.setSize(SETTINGS.HEALTHBAR_PLAYER_WIDTH, SETTINGS.HEALTHBAR_PLAYER_HEIGHT);
        maxWidth = SETTINGS.HEALTHBAR_PLAYER_WIDTH;
        width = maxWidth;
    }

    @Override
    public void update(float dt) {
        // no updates to pos, always fixed at top
    }
}
