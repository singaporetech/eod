package com.boliao.eod.components;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Movement extends Component {
    private static final String TAG = "Movement:Component";

    private Transform transform;
    private com.boliao.eod.components.collision.Collider collider;

    private Vector2 acc = new Vector2();
    private Vector2 vel = new Vector2();
    private Vector2 disp = new Vector2();
    private Vector2 dir = new Vector2();
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
        collider = (com.boliao.eod.components.collision.Collider) owner.getComponent("Collider");

        // init vel
        vel.setZero();
    }

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }

    public Vector2 getVel() {
        return vel;
    }

    public void faceTargetPos(Vector2 pos) {
        transform.setForward(pos.sub(transform.getPos()).nor());
    }

    /**
     * AI: steering
     * 2. Using the force to update position
     * - don't forget your classical mechanics
     * @return
     */
    public void move(float dt, Vector2 force) {
        // calc acc
        acc.set(force).scl(1/mass);

        if (acc.len2() > 0) {
            // update vel
            vel.add(acc.scl(dt));

            // update collider
            collider.setCollisionVecLen(vel.len() * SETTINGS.COLLISION_FORWARD_LEN);

            // update position
            disp.set(vel).scl(dt);
            transform.translate(disp);

            // update rotation
            dir.set(vel).nor();
            transform.setForward(dir);

            //Gdx.app.log(TAG, "vel=" + vel + " acc=" + acc + " disp=" + displacement);
        }
        else {
            vel.setZero();
        }
    }
}
