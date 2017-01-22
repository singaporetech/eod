package com.boliao.eod.components;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 22/1/17.
 */

public class SteeringSeek extends Steering {
    protected float maxSpeed = SETTINGS.HUMAN_SPEED;
    protected float forceMult = SETTINGS.FORCE_MULT;

    public SteeringSeek() {
        super("SteeringSeek");
    }

    @Override
    public Vector2 getForce() {
        // calc desired velocity
        Vector2 force = new Vector2(dir);
        force.scl(maxSpeed);

        // calc force
        force.sub(movement.getVel()).scl(forceMult);
        return force;
    }
}
