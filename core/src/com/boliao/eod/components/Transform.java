package com.boliao.eod.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Transform extends Component {
    protected Vector2 pos;
    protected float rot;
    protected Vector2 forward;

    public Transform(int x, int y, int r) {
        super("Transform");

        pos = new Vector2(x, y);
        rot = r;
        forward = new Vector2(1,0).rotate(r);
    }

    public void setPos(Vector2 pos) {
        this.pos.set(pos);
    }

    public Vector2 getPos() {
        return pos;
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public float getRot() {
        return rot;
    }

    public void setRot(float r) {
        rot = r;
        forward.set(1,0).rotate(r);
    }

    public void setForward(Vector2 vec) {
        forward.set(vec);
        rot = MathUtils.atan2(vec.y, vec.x); // todo: check if this is correct
    }

    public void translate(float x, float y) {
        pos.add(x, y);
    }

    public void translate(Vector2 vec) {
        pos.add(vec);
    }

    public void rotate(int r) {
        rot += r;
        forward.rotate(r);
    }

    public Vector2 getForward() {
        return forward;
    }
}
