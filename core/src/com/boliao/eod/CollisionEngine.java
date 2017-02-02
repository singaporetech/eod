package com.boliao.eod;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.components.Collidable;
import com.boliao.eod.components.Collider;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 24/1/17.
 */

public class CollisionEngine implements Engine {
    private List<Collidable> collidables = new LinkedList<Collidable>();

    private static CollisionEngine instance = new CollisionEngine();
    public static CollisionEngine i(){
        return instance;
    }
    private CollisionEngine() {}

    public void tick() {
        // do collision responses to prevent overlapping objects
        // todo: do quadtrees when things grow big
//        for (Collidable c1: collidables) {
//            for (Collidable c2: collidables) {
//                if (c1 != c2 && c2.isStatic()) {
//                    c1.checkCollisionAndRespond(c2);
//                }
//            }
//        }
    }

    @Override
    public void init() {

    }

    /**
     * Check if collider has collided with any other collidables
     * @param collider
     * @return
     */
    public Vector2 getCollisionNorm(Collider collider) {
        for (Collidable c: collidables) {
            if (collider != (Collider) c) {
                Vector2 collisionNorm = collider.getCollisionNorm(c);
                if (collisionNorm != null) {
                    return collisionNorm;
                }
            }
        }
        return null;
    }

    /**
     * Get a new target offset from the obstacle, for steering purposes
     * @param collider
     * @return
     */
    public Vector2 getCollisionAvoidTarget(Collider collider) {
        for (Collidable c: collidables) {
            if (collider != (Collider) c) {
                Vector2 target = collider.getCollisionAvoidTarget(c);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    public void addCollidable(Collidable c) {
        collidables.add(c);
    }

    @Override
    public void finalize() {

    }
}
