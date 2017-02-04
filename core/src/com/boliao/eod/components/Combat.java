package com.boliao.eod.components;

import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.render.SpriteBam;
import com.boliao.eod.components.render.SpriteSheet;

/**
 * Created by mrboliao on 3/2/17.
 */

public class Combat extends Component{
    private static final String TAG = "Combat:C";

    Transform transform;
    SpriteSheet spriteSheet;
    SpriteBam spriteBam;

    GameObject targetGO;
    Health targetHealth;
    Transform targetTransform;

    protected float dmg = SETTINGS.BUG_DMG;
    protected float delayTime = SETTINGS.ATTACK_DELAY_TIME;
    protected float timeElapsed = 0;

    public Combat(GameObject targetGO) {
        super("Combat");

        this.targetGO = targetGO;
        disable();
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");
        spriteSheet = (SpriteSheet) owner.getComponent("SpriteSheet");
        spriteBam = (SpriteBam) owner.getComponent("SpriteBam");
        targetTransform = (Transform) targetGO.getComponent("Transform");
        targetHealth = (Health) targetGO.getComponent("Health");
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        //Gdx.app.log(TAG, "timeElapsed=" + timeElapsed + " delta=" + delta);

        if (isActive) {
            timeElapsed += delta;
            if (timeElapsed >= delayTime) {
                targetHealth.hit(dmg);
                spriteBam.reset();
                timeElapsed = 0;
            }
        }

        // fade sprite and set position
        spriteBam.setPos(targetTransform.getPos());
        if (spriteBam.getAlpha() > 0) {
            spriteBam.shrinkAndFade(delta, SETTINGS.BAM_FADEOUT_DECREMENT);
        }
    }
}
