package com.boliao.eod.components.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.*;
import com.boliao.eod.components.render.Renderable;
import com.boliao.eod.components.render.RenderableDebug;

/**
 * Created by mrboliao on 24/1/17.
 */

public class Collider extends Component implements Collidable, RenderableDebug {
    private static final String TAG = "Collider:C";

    Transform transform;
    Renderable renderable;

    // flags
    boolean isStatic = true;
    boolean isCollidable = true;

    //Polygon boundingPolygon;
    Circle boundingCircle;
    Vector2 collisionNorm = new Vector2(0, 0);
    Vector2 collisionAvoidTarget = new Vector2(0, 0);
    float collisionMag = 0;

    // collision vectors
    // - using a fan
    Vector2 collisionVec = new Vector2();
    Vector2 collisionVec0 = new Vector2(); // equals length of bounding curcle
    Vector2 collisionVecL = new Vector2();
    Vector2 collisionVecR = new Vector2();
    Vector2 collisionForwardPos = new Vector2();
    Vector2 collisionForwardPos0 = new Vector2();
    Vector2 collisionForwardPosL = new Vector2();
    Vector2 collisionForwardPosR = new Vector2();
    Vector2 prevCollisionForwardPos = collisionForwardPosL;
    float collisionVecLen = SETTINGS.COLLISION_FORWARD_LEN;

    public Collider() {
        this(true, true);
    }
    public Collider(boolean isCollidable, boolean isStatic) {
        super("Collider");
        this.isCollidable = isCollidable;
        this.isStatic = isStatic;
    }

    @Override
    public GameObject getOwner() {
        return owner;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");

        // todo: do sprite sheet if no sprite, perhaps need a way to get only interface
        renderable = owner.getRenderable();

        // init bounding circle
        Rectangle rect = renderable.getBoundingBox();
        boundingCircle = new Circle(transform.getPos(), rect.getWidth()/2);

        // add to collision engine
        CollisionEngine.i().addCollidable(this);
        RenderEngine.i().addRenderableDebug(this);
    }

    public Vector2 getBoundingCirclePos() {
        return new Vector2(boundingCircle.x, boundingCircle.y);
    }

    public float getBoundingCircleRadius() {
        return boundingCircle.radius;
    }

    public Vector2 getCollisionAvoidTarget() {
        return collisionAvoidTarget;
    }

    public void setCollisionVecLen(float collisionVecLen) {
        this.collisionVecLen = collisionVecLen;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean isCollidable() {
        return isCollidable;
    }

    public float getCollisionMag() {
        return collisionMag;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // bounding circle tp match transform position and rotation
        boundingCircle.setPosition(transform.getPos());
        collisionVec.set(transform.getForward()).scl(collisionVecLen);
        collisionVec0.set(transform.getForward()).scl(getBoundingCircleRadius());
        collisionVecL.set(collisionVec).rotate(SETTINGS.COLLISION_VEC_OFFSET_DEG);
        collisionVecR.set(collisionVec).rotate(-SETTINGS.COLLISION_VEC_OFFSET_DEG);
        collisionForwardPos.set(transform.getPos()).add(collisionVec);
        collisionForwardPos0.set(transform.getPos()).add(collisionVec0);
        collisionForwardPosL.set(transform.getPos()).add(collisionVecL);
        collisionForwardPosR.set(transform.getPos()).add(collisionVecR);
    }

    @Override
    /**
     * todo: draw the debug bounds
     */
    public void draw() {
        if (SETTINGS.IS_DEBUG) {
            RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Line);

            // draw bounding circle
            RenderEngine.i().getDebugRenderer().setColor(0, 1, 0, 1);
            RenderEngine.i().getDebugRenderer().circle(boundingCircle.x, boundingCircle.y, boundingCircle.radius);

            // draw the collision forward vectors
            RenderEngine.i().getDebugRenderer().setColor(1, 1, 1, 1);
            RenderEngine.i().getDebugRenderer().line(transform.getPos(), collisionForwardPos);
            RenderEngine.i().getDebugRenderer().setColor(1, 1, 1, 1);
            RenderEngine.i().getDebugRenderer().line(transform.getPos(), collisionForwardPosL);
            RenderEngine.i().getDebugRenderer().setColor(1, 1, 1, 1);
            RenderEngine.i().getDebugRenderer().line(transform.getPos(), collisionForwardPosR);


            RenderEngine.i().getDebugRenderer().end();

            if (!collisionAvoidTarget.equals(Vector2.Zero)) {
                RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Filled);

                // draw the collision avoid target
                RenderEngine.i().getDebugRenderer().setColor(1, 1, 1, 1);
                RenderEngine.i().getDebugRenderer().circle(collisionAvoidTarget.x, collisionAvoidTarget.y, 5);

                RenderEngine.i().getDebugRenderer().end();
            }
        }
    }

    @Override
    public Rectangle getBoundingBox() {
        return renderable.getBoundingBox();
    }

    /**
     * Get the collision position, i.e., centroid of collided polygon
     * @param other
     * @return null if no intersection
     */
    @Override
    public Vector2 getCollisionNorm(Collidable other) {
        Vector2 disp = new Vector2();
        float dist;

        // calc collision magnitude
        // - closer means stronger
        collisionMag = SETTINGS.COLLISION_FORCE * 1/getBoundingCirclePos().dst(other.getBoundingCirclePos());

        // check collision left fan edge
        dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPosL, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), disp);
        if (dist != Float.POSITIVE_INFINITY) {
            prevCollisionForwardPos = collisionForwardPosL;
            collisionNorm.set(collisionForwardPosR).sub(other.getBoundingCirclePos()).nor();

            Gdx.app.log(TAG, owner.getName() + ": COLLIDED LEFT: pos=" + transform.getPos() + " collisionMag=" + collisionMag + " colNorm=" + collisionNorm);

            return collisionNorm;
        }

        // check collision right fan edge
        dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPosR, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), disp);
        if (dist != Float.POSITIVE_INFINITY) {
            prevCollisionForwardPos = collisionForwardPosR;
            collisionNorm.set(collisionForwardPosL).sub(other.getBoundingCirclePos()).nor();

            Gdx.app.log(TAG, owner.getName() + ": COLLIDED RIGHT: pos=" + transform.getPos() + " collisionMag=" + collisionMag + " colNorm=" + collisionNorm);

            return collisionNorm;
        }

        // check with center edge
        dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPos, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), disp);
        if (dist != Float.POSITIVE_INFINITY) {
            collisionNorm.set(prevCollisionForwardPos).sub(other.getBoundingCirclePos()).nor();

            Gdx.app.log(TAG, owner.getName() + ": COLLIDED CENTER: pos=" + transform.getPos() + " collisionMag=" + collisionMag + " colNorm=" + collisionNorm);

            return collisionNorm;
        }

        // else no collisions
        return null;
    }

    public Vector2 getCollisionAvoidTarget(Collidable other) {
        Vector2 disp = new Vector2();
        float dist;

        // calc collision magnitude
        collisionMag = SETTINGS.COLLISION_TARGET_OFFSET + getBoundingCircleRadius();

        // check collision left fan edge
        dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPosL, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), disp);
        if (dist != Float.POSITIVE_INFINITY) {
            prevCollisionForwardPos = collisionForwardPosL;
            collisionNorm.set(collisionForwardPosR).sub(other.getBoundingCirclePos()).nor();
            collisionAvoidTarget.set(collisionNorm).scl(collisionMag).add(other.getBoundingCirclePos());

            // Gdx.app.log(TAG, owner.getName() + ": COLLIDED LEFT: pos=" + transform.getPos() + " collisionMag=" + collisionMag + " collisionAvoidTarget=" + collisionAvoidTarget);

            return collisionAvoidTarget;
        }

        // check collision right fan edge
        dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPosR, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), disp);
        if (dist != Float.POSITIVE_INFINITY) {
            prevCollisionForwardPos = collisionForwardPosR;
            collisionNorm.set(collisionForwardPosL).sub(other.getBoundingCirclePos()).nor();
            collisionAvoidTarget.set(collisionNorm).scl(collisionMag).add(other.getBoundingCirclePos());

            // Gdx.app.log(TAG, owner.getName() + ": COLLIDED RIGHT: pos=" + transform.getPos() + " collisionMag=" + collisionMag + " collisionAvoidTarget=" + collisionAvoidTarget);

            return collisionAvoidTarget;
        }

        // check with center edge
        dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPos, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), disp);
        if (dist != Float.POSITIVE_INFINITY) {
            collisionNorm.set(prevCollisionForwardPos).sub(other.getBoundingCirclePos()).nor();
            collisionAvoidTarget.set(collisionNorm).scl(collisionMag).add(other.getBoundingCirclePos());

            // Gdx.app.log(TAG, owner.getName() + ": COLLIDED CENTER: pos=" + transform.getPos() + " collisionMag=" + collisionMag + " collisionAvoidTarget=" + collisionAvoidTarget);

            return collisionAvoidTarget;
        }

        // else no collisions
        return null;
    }

    @Override
    public boolean collidedWithPos(Vector2 pos) {
        return boundingCircle.contains(pos);
    }

    @Override
    public void finalize() {
        super.finalize();
    }
}
