package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 24/1/17.
 * - implements Renderable for debugging purposes
 */

public class SteeringPursueCollision extends SteeringPursue implements RenderableDebug {
    private static final String TAG = "SteeringPursueC:C";

    protected Vector2 forwardPos = new Vector2();
    protected Vector2 pursueForce = new Vector2();
    protected Vector2 collisionForce = new Vector2();
    protected Vector2 collisionPos = new Vector2();
    protected Vector2 pursuePos = new Vector2();

    /**
     * Use same name as super so that Fsm can be uniform.
     */
    public SteeringPursueCollision(GameObject targetGO) {
        super(targetGO);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        RenderEngine.i().addRenderableDebug(this);
    }

    @Override
    public Vector2 getForce() {
        // setup forward vector
//        Vector2 vec = new Vector2(dir).scl(SETTINGS.COLLISION_FORWARD_LEN);
//        forward.set(transform.getPos()).add(vec);
        forwardPos.set(dir).scl(SETTINGS.COLLISION_FORWARD_LEN).add(transform.getPos());

        // get pursue force
        pursueForce.set(super.getForce());

        // check collision
        Collidable collidable = CollisionEngine.i().getColliderWithLine(transform.getPos(), forwardPos);
        if (collidable != null) {
            collisionForce.set(collidable.getCollisionDir(forwardPos)).scl(SETTINGS.COLLISION_FORCE);
            Vector2 resultantForce = new Vector2().set(pursueForce).scl(0.5f).add(collisionForce.scl(0.5f));
            Gdx.app.log(TAG, "COLLIDED: pos=" + transform.getPos() + " forwardPos=" + forwardPos + " collF=" + collisionForce + " resF=" + resultantForce);
            return collisionForce;
        }
        return pursueForce;
    }

    @Override
    public void draw() {
        RenderEngine.i().getDebugRenderer().setColor(1,0,0,1);
        RenderEngine.i().getDebugRenderer().line(transform.getX(), transform.getY(), forwardPos.x, forwardPos.y);
        RenderEngine.i().getDebugRenderer().setColor(0,1,0,1);
        collisionPos.set(transform.getPos()).add(collisionForce);
        RenderEngine.i().getDebugRenderer().line(transform.getX(), transform.getY(), collisionPos.x, collisionPos.y);
        RenderEngine.i().getDebugRenderer().setColor(0,0,1,1);
        pursuePos.set(transform.getPos()).add(pursueForce);
        RenderEngine.i().getDebugRenderer().line(transform.getX(), transform.getY(), pursuePos.x, pursuePos.y);

//        Gdx.app.log(TAG, "DRAW: forwardPos=" + forwardPos + " collF=" + collisionForce + " purF=" + pursueForce);

    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }
}
