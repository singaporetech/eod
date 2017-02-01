package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 25/1/17.
 */

public class SteeringArriveCollision extends SteeringArrive {
    private static final String TAG = "SteeringArriveCollision:C";

    protected Vector2 forwardVec = new Vector2();
    protected Vector2 forwardPos = new Vector2();
    protected Vector2 collisionPos = new Vector2();

    protected Vector2 parentForce = new Vector2();
    protected Vector2 collisionForce = new Vector2();

    protected Collider collider;

    public SteeringArriveCollision() {
        super("SteeringArriveCollision");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        collider = (Collider) owner.getComponent("Collider");
    }

    @Override
    public Vector2 getForce() {
        // get arrive force (which will also update direction vec
        parentForce.set(super.getForce());

        // get collided position
        Vector2 collisionNorm = CollisionEngine.i().getCollisionNorm(collider);

        // if there is a collision position
        if (collisionNorm != null) {
            // calc collision force
            Vector2 vel = new Vector2(movement.getVel()).nor();
            collisionForce.set(collisionNorm).sub(vel).scl(collider.getCollisionMag());

            Vector2 resultantForce = new Vector2().set(parentForce).scl(0.3f).add(collisionForce.scl(0.7f));

            //Gdx.app.log(TAG, "COLLIDED: pos=" + transform.getPos() + " parF=" + parentForce + " collF=" + collisionForce + " resF=" + resultantForce);

//            return collisionForce;
            return resultantForce;
        }
        return parentForce;
    }
}
