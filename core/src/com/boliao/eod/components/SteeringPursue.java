package com.boliao.eod.components;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 23/1/17.
 */

public class SteeringPursue extends SteeringArrive {
    private Transform targetTransform;

    // override stop radius
    private float stopRadius = SETTINGS.STOP_RADIUS_BUG;
    protected float maxSpeed = SETTINGS.SPEED_BUG;

    public SteeringPursue(GameObject targetGO) {
        super("SteeringPursue");

        //set target transform
        targetTransform = (Transform) targetGO.getComponent("Transform");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // set destPos by ref
        destPos = targetTransform.getPos();
        updateDirAndDist();
    }

    public boolean targetGotAway() {
        updateDirAndDist();
        if (dist > SETTINGS.MELEE_RANGE) {
            return true;
        }
        return false;
    }
}
