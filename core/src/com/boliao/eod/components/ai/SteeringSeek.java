package com.boliao.eod.components.ai;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 22/1/17.
 */

public class SteeringSeek extends Steering {
    protected float maxSpeed = SETTINGS.SPEED_PLAYER;
    protected float forceMult = SETTINGS.FORCE_MULT;

    public SteeringSeek() {
        super("SteeringSeek");
    }

    public SteeringSeek(String name) {
        super (name);
    }

    /**
     * AI: steering
     * 1. Calculating force for each frame.
     * - goto Movement component for actually using this force
     * @return
     */
    @Override
    public Vector2 getForce() {
        // update direction and distance
        updateDirAndDist();
        Vector2 force = new Vector2(dir);

        // calc desired velocity
        force.scl(maxSpeed);

        // calc force
        force.sub(movement.getVel()).scl(forceMult);

        return force;
    }

    @Override
    public Vector2 getBaseForce() {
        return null;
    }
}
