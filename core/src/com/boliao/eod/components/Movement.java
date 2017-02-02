package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Movement extends Component {
    private static final String TAG = "Movement:Component";

    private Transform transform;
    private Collider collider;

    private Vector2 vel = new Vector2();
    private float mass = SETTINGS.MASS;
    private float speed = SETTINGS.SPEED_PLAYER; //todo: need to match the speed of steering

    public Movement() {
        super("Movement");
    }
    public Movement(float speed) {
        super("Movement");
        this.speed = speed;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");
        collider = (Collider) owner.getComponent("Collider");

        // init vel
        vel.setZero();
    }

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }

    public Vector2 getVel() {
        return vel;
    }

    public void move(float delta, Vector2 force) {
        // calc acc
        Vector2 acc = new Vector2(force);
        acc.scl(1/mass);

        // update vel
        if (acc.len2() > 0) {
            vel.add(acc.scl(delta));

            // clip to maxSpeed
            // todo: is this needed?
//            if (vel.len2() > speed*speed)
//                vel.nor().scl(speed);

            // update collider
            collider.setCollisionVecLen(vel.len() * SETTINGS.COLLISION_FORWARD_LEN);

            // update position
            Vector2 displacement = new Vector2(vel);
            displacement.scl(delta);
            transform.translate(displacement);

            // update rotation
            //if (vel.x != 0 && vel.y != 0) {
                transform.setForward(new Vector2(vel).nor());

            //}
            //Gdx.app.log(TAG, "vel=" + vel + " acc=" + acc + " disp=" + displacement);
        }
        else {
            vel.setZero();
        }
    }
}
