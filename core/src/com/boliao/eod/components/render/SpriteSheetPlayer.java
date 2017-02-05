package com.boliao.eod.components.render;

import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 6/2/17.
 */

public class SpriteSheetPlayer extends SpriteSheet {
    public SpriteSheetPlayer(String spritePath) {
        super("SpriteSheetPlayer", spritePath, SETTINGS.SPRITE_SIZE);
    }

    public void setSequence(Sequence seq) {
        this.sequence = seq;
        switch(seq) {
            case RUN:
                startFrame = 0;
                endFrame = 2;
                break;

            case MELEE:
                startFrame = 0;
                endFrame = 2;
                break;
        }
    }
}
