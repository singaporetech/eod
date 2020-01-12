package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.render.SpriteBam;
import com.boliao.eod.components.render.SpriteSheet;

/**
 * Created by mrboliao on 3/2/17.
 */

public class Combat extends Component{
    private String TAG = "Combat:C";

    Transform transform;
    SpriteSheet spriteSheet;
    SpriteBam spriteBam;

    GameObject targetGO;
    Health targetHealth;
    Transform targetTransform;

    protected float dmg;
    protected float delayTime = SETTINGS.ATTACK_DELAY_TIME;
    protected float timeElapsed = 0;

    public Combat(GameObject targetGO, float dmg) {
        super("Combat");

        this.targetGO = targetGO;
        this.dmg = dmg;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);
        TAG += ":" + owner.getName();

        transform = (Transform) owner.getComponent("Transform");
        spriteSheet = (SpriteSheet) owner.getComponent("SpriteSheet");
        spriteBam = (SpriteBam) owner.getComponent("SpriteBam");

        if (targetGO != null) {
            targetTransform = (Transform) targetGO.getComponent("Transform");
            targetHealth = (Health) targetGO.getComponent("Health");
        }

        disable();
    }

    @Override
    public void enable() {
        super.enable();
        spriteBam.enable();
    }

    @Override
    public void disable() {
        super.disable();
        spriteBam.disable();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // need to recheck targethealth == null as sometimes fsm not fast enough to detect player destroyed
        if (isActive && (targetHealth != null)) {
            timeElapsed += dt;
            if (timeElapsed >= delayTime) {
                targetHealth.hit(dmg);
                spriteBam.reset();
                timeElapsed = 0;
            }

            // fade sprite and set position
            spriteBam.setPos(targetTransform.getPos());
            spriteBam.shrinkAndFade(dt, SETTINGS.BAM_FADEOUT_DECREMENT);

            //Gdx.app.log(TAG, "timeElapsed=" + timeElapsed + " spriteBam active=" + spriteBam.isActive + " alpha=" + spriteBam.getAlpha());
        }
    }

    public void setTarget(GameObject targetGO) {
        this.targetGO = targetGO;
        targetTransform = (Transform) targetGO.getComponent("Transform");
        targetHealth = (Health) targetGO.getComponent("Health");
    }

    public boolean isTargetDestroyed() {
        return (targetHealth == null) || targetHealth.isEmpty();
    }

    public Vector2 getTargetPos() {
        if (targetTransform != null) {
            return new Vector2(targetTransform.getPos());
        }
        else {
            Gdx.app.log(TAG, "attempting to get targetTransform when no target.");
            return null;
        }
    }


    public boolean hasTarget() {
        return targetGO != null;
    }



    public void releaseTarget() {
        targetGO = null ;
        targetTransform = null;
        targetHealth = null;
    }
}
