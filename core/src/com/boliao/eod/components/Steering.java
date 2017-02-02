package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 22/1/17.
 */

public abstract class Steering extends Component {
    private static final String TAG ="Steering:C";

    protected Transform transform;
    protected Movement movement;

    protected Vector2 destPos;
    protected Vector2 dir = new Vector2();
    protected float dist;

    // steering params
    protected float stopRadius = SETTINGS.STOP_RADIUS;

    public Steering(String name) {
        super(name);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");
        movement = (Movement) owner.getComponent("Movement");

        // init vectors
        destPos = new Vector2(transform.getX(), transform.getY());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void setDestPos(float x, float y) {
        // todo: steering
        destPos.set(x, y);

        // calc dir and dist
        updateDirAndDist();
    }

    public void setDestPos(Vector2 v) {
        setDestPos(v.x, v.y);
    }

    public Vector2 getDestPos() {
        return destPos;
    }

    public boolean reachedDestPos() {
        //Gdx.app.log(TAG, "dist=" + dist + " pos=" + transform.pos + " destPos=" + destPos);

        if (dist < stopRadius) {
            //todo: see whether got more elegant way to accurately stop near touch point
            //transform.setPos(destPos);
            // face the destPos

            // face the destination
            // todo: need to gradually turn to face destPos (now simply increase STOP_RAD
            //transform.rot = MathUtils.radiansToDegrees * (float) Math.atan2(dir.y, dir.x);

            return true;
        }
        else {
            return false;
        }
    }

    protected void updateDirAndDist() {
        dir.set(destPos).sub(transform.pos);
        dist = dir.len();
        dir.nor();
    }

    public abstract Vector2 getForce();
    public abstract Vector2 getBaseForce();
}
