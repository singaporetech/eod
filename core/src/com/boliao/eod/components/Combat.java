package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.render.Renderable;
import com.boliao.eod.components.render.SpriteSheet;

/**
 * Created by mrboliao on 3/2/17.
 */

public class Combat extends Component implements Renderable{
    private static final String TAG = "Combat:C";

    Transform transform;
    SpriteSheet spriteSheet;

    GameObject targetGO;
    Health targetHealth;

    protected float dmg = SETTINGS.BUG_DMG;
    protected float delayTime = SETTINGS.ATTACK_DELAY_TIME;
    protected float timeElapsed = 0;

    private com.badlogic.gdx.graphics.g2d.Sprite sprite;
    private float spriteAlpha = 1;

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
                timeElapsed = 0;
            }
        }
    }

    @Override
    public void draw() {

    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }
}
