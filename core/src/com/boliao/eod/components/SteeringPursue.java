package com.boliao.eod.components;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 23/1/17.
 */

public class SteeringPursue extends SteeringArrive {
    private Transform targetTransform;

    private GameObject targetGO;

    public SteeringPursue(GameObject targetGO) {
        super("SteeringPursue");

        this.targetGO = targetGO;

        // override params
        stopRadius = SETTINGS.STOP_RADIUS_PURSUE;
        maxSpeed = SETTINGS.SPEED_BUG;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        //set target transform
        targetTransform = (Transform) targetGO.getComponent("Transform");

        destPos.set(targetTransform.getPos());
        updateDirAndDist();
    }

    public boolean targetGotAway() {
        destPos.set(targetTransform.getPos());
        updateDirAndDist();
        if (dist > SETTINGS.MELEE_RANGE) {
            return true;
        }
        return false;
    }

    @Override
    public Vector2 getForce() {
        destPos.set(targetTransform.getPos());
        return super.getForce();
    }

    @Override
    public Vector2 getBaseForce() {
        return super.getForce();
    }
}
