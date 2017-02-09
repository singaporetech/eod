package com.boliao.eod.components.ai;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.*;

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
        if (targetGO != null) {
            setTarget(targetGO);
        }
    }

    public void setTarget(GameObject targetGO) {
        if (!targetGO.equals(this.targetGO)) {
            this.targetGO = targetGO;
        }
        targetTransform = (Transform) targetGO.getComponent("Transform");
        setDestPos(targetTransform.getPos());
    }

    public boolean targetGotAway() {
        setDestPos(targetTransform.getPos());
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
