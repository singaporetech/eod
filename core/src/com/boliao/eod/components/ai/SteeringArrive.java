package com.boliao.eod.components.ai;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 23/1/17.
 */

public class SteeringArrive extends SteeringSeek {
    private static final String TAG = "SteeringArrive:C";

    private float slowRadius = SETTINGS.SLOW_RADIUS;
    private float decelFactor = SETTINGS.DECEL_FACTOR;
    private float minSpeed = SETTINGS.MIN_SPEED;

    public SteeringArrive() {
        super("SteeringArrive");
    }

    public SteeringArrive(String name) {
        super(name);
    }

    @Override
    public Vector2 getForce() {
        // update direction and distance
        updateDirAndDist();
        Vector2 force = new Vector2(dir);

        // if distant, do rest of seek and return
        if (dist > slowRadius) {
            // calc desired velocity
            force.scl(maxSpeed);

            // calc force
            force.sub(movement.getVel()).scl(forceMult);

            //Gdx.app.log(TAG, "FAR AWAY: dist=" + dist + " force=" + force + " pos=" + transform.pos);
            return force;
        }

        // if close enough, save direction and return 0 force
        else if (dist < stopRadius) {
            //Gdx.app.log(TAG, "IN STOP RAD: dist=" + dist + " force=" + force + " pos=" + transform.pos);
            return new Vector2(0, 0);
        }

        // else is within slowRadius, so decelerate
        else {
            // scale speed according to distance left
            // - nearer means slower
            float speed = maxSpeed * dist / slowRadius / decelFactor;

            //float speed = movement.getVel().len() * dist / slowRadius / 50;

            // clip to start speed
            // - when destination is very near, it will take too long
            if (speed < minSpeed) {
                speed = minSpeed;
            }

            // calc desired velocity
            force.scl(speed);

            //Gdx.app.log(TAG, "IN SLOW RAD: dist=" + dist + " desiredVel=" + force + " currVel=" + movement.getVel());

            // calc force
            force.sub(movement.getVel()).scl(forceMult);

            //Gdx.app.log(TAG, "IN SLOW RAD: dist=" + dist + " speed=" + speed + " force=" + force + " pos=" + transform.pos);
            return force;
        }
    }

    public Vector2 getBaseForce() {
        return super.getForce();
    }
}
