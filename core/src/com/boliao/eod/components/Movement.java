package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Movement extends Component {
    private static final String TAG = "Movement:Component";

    private Transform transform;

    private Vector2 destPos;
    private Vector2 dir;

    public Movement() {
        super("Movement");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");

        // init vectors
        destPos = new Vector2(transform.getX(), transform.getY());
        dir = new Vector2();
    }

    public void setDestPos(float x, float y) {
        Gdx.app.log(TAG,"BEFORE destPos="+destPos+" dir="+dir+" pos="+transform.pos+" x="+x+" y="+y);

        // todo: steering
        destPos.set(x, y);


        dir.set(destPos).sub(transform.pos).nor();

        Gdx.app.log(TAG,"AFTER destPos="+destPos+" dir="+dir+" pos="+transform.pos);
    }

    public boolean reachedDestPos() {
        if (transform.pos.dst2(destPos) < SETTINGS.DIST_THRES) {
            transform.setPos(destPos);
            return true;
        }
        else {
            return false;
        }
    }

    public void moveInDir(float delta) {
        //Gdx.app.log(TAG, "transform.pos=" + transform.pos + "delta=" + delta + "dir=" + dir);
        float mul = SETTINGS.HUMAN_SPEED * delta;
        transform.translate(dir.x*mul, dir.y*mul);
        //Gdx.app.log(TAG, "distanceLeftSq=" + transform.pos.dst2(destPos));
        //Gdx.app.log(TAG, "transform.pos=" + transform.pos + "delta=" + delta + "dir=" + dir);
    }
}
