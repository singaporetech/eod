package com.boliao.eod.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 22/1/17.
 */

public abstract class Steering extends Component {
    protected Transform transform;
    protected Movement movement;

    protected Vector2 destPos;
    protected Vector2 dir;

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
        dir = new Vector2();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void setDestPos(float x, float y) {
        // todo: steering
        destPos.set(x, y);
        dir.set(destPos).sub(transform.pos).nor();
    }

    public void setDestPos(Vector2 v) {
        destPos.set(v);
        dir.set(destPos).sub(transform.pos).nor();
        transform.rot = MathUtils.radiansToDegrees * (float) Math.atan2(dir.y, dir.x);
    }

    public Vector2 getDestPos() {
        return destPos;
    }

    public boolean reachedDestPos() {
        if (transform.pos.dst2(destPos) < SETTINGS.PROXIMITY2) {
            transform.setPos(destPos);
            return true;
        }
        else {
            return false;
        }
    }

    public abstract Vector2 getForce();
}
