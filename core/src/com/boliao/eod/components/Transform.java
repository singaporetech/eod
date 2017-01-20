package com.boliao.eod.components;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Transform extends Component {
    protected Vector2 pos;
    protected int rot;

    public Transform(int x, int y, int r) {
        super("Transform");

        pos = new Vector2(x, y);
        rot = r;
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


    public void translate(float x, float y) {
        pos.add(x, y);
    }

    public void translate(Vector2 vec) {
        pos.add(vec);
    }

    public void rotate(int r) {
        rot += r;
    }
}
