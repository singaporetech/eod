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

public class SteeringPursueCollision extends SteeringPursue implements Renderable {
    private static final String TAG = "SteeringPursueC:C";

    protected Vector2 forwardPos = new Vector2();
    protected ShapeRenderer shapeRenderer = new ShapeRenderer();

    /**
     * Use same name as super so that Fsm can be uniform.
     */
    public SteeringPursueCollision(GameObject targetGO) {
        super(targetGO);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        RenderEngine.i().addRenderable(this);
        shapeRenderer.setProjectionMatrix(RenderEngine.i().getCam().combined);
    }

    @Override
    public Vector2 getForce() {
        // setup forward vector
//        Vector2 vec = new Vector2(dir).scl(SETTINGS.COLLISION_FORWARD_LEN);
//        forward.set(transform.getPos()).add(vec);
        forwardPos.set(dir).scl(SETTINGS.COLLISION_FORWARD_LEN).add(transform.getPos());

        // check collision
        Collidable collidable = CollisionEngine.i().getColliderWithLine(transform.getPos(), forwardPos);
        if (collidable != null) {
            Vector2 resultantForce = super.getForce().add(collidable.getCollisionDir(forwardPos)).scl(SETTINGS.COLLISION_FORCE);
            Gdx.app.log(TAG, "COLLIDED: pos=" + transform.getPos() + " forwardPos=" + forwardPos + " resultantForce=" + resultantForce);
            return resultantForce;

        }
        return super.getForce();
    }

    @Override
    public void draw() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0,0.5f,0.5f,1.0f);
        //shapeRenderer.line(transform.getPos().x, transform.getPos().y,forwardPos.x, forwardPos.y);
        shapeRenderer.line(0,0,800,800);
        shapeRenderer.end();
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }
}
