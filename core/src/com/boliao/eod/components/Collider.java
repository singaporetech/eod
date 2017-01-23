package com.boliao.eod.components;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.Game;
import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 24/1/17.
 */

public class Collider extends Component implements Collidable, Renderable {
    private static final String TAG = "Collider:C";

    Transform transform;
    Renderable renderable;

    Polygon boundingPolygon;

    public Collider() {
        super("Collider");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");
        // todo: do sprite sheet if no sprite, perhaps need a way to get only interface
        renderable = (Renderable) owner.getComponent("Sprite");

        // init polygon
        Rectangle rect = renderable.getBoundingBox();
        boundingPolygon = new Polygon(new float[]{
                0,0,
                rect.width,0,
                rect.width,rect.height,
                0,rect.height,
                0,0
        });
        boundingPolygon.setOrigin(rect.width/2, rect.height/2);

        // add to collision engine
        CollisionEngine.i().addCollidable(this);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // match transform position and rotation
        boundingPolygon.setPosition(transform.getX(), transform.getY());
        boundingPolygon.setRotation(transform.getRot());

        // todo: put in testcase
//        boolean aaa = hasCollidedWithLine(new Vector2(380,0), new Vector2(380, 1000));
//        boolean bbb = hasCollidedWithLine(new Vector2(379,0), new Vector2(379, 1000));
//        boolean aac = hasCollidedWithLine(new Vector2(420,0), new Vector2(420, 1000));
//        boolean bba = hasCollidedWithLine(new Vector2(0,301), new Vector2(390, 301));
//        boolean abb = hasCollidedWithLine(new Vector2(0,0), new Vector2(500, 500));
//        boolean bca = hasCollidedWithLine(new Vector2(0,299), new Vector2(20, 299));
    }

    @Override
    /**
     * todo: draw the debug bounds
     */
    public void draw() {
        //Game.i().spriteBatch.draw();
    }

    @Override
    public Rectangle getBoundingBox() {
        return renderable.getBoundingBox();
    }

    @Override
    public boolean hasCollidedWithLine(Vector2 pos1, Vector2 pos2) {
        return Intersector.intersectSegmentPolygon(pos1, pos2,boundingPolygon);
    }

    @Override
    public Vector2 getCollisionDir(Vector2 forwardPos) {
        return new Vector2(forwardPos).sub(transform.getPos()).nor();
    }
}
