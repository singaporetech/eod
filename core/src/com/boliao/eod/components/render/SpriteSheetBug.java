package com.boliao.eod.components.render;

import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 6/2/17.
 */

public class SpriteSheetBug extends SpriteSheet {

    public SpriteSheetBug(String spritePath) {
        super("SpriteSheetBug", spritePath, SETTINGS.SPRITE_WIDTH, SETTINGS.SPRITE_HEIGHT);
    }

    @Override
    public void setSequence(Sequence seq) {
        this.sequence = seq;
        switch(seq) {
            case RUN:
                startFrame = 0;
                endFrame = 3;
                break;

            case MELEE:
                startFrame = 4;
                endFrame =8;
                break;

            case DESTRUCT:
                startFrame = 9;
                endFrame =10;
                break;
        }
        currSpriteIndex = startFrame;
    }
}
