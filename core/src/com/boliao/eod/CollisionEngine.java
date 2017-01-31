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
//        for (Collidable c: collidables) {
//
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

    public void addCollidable(Collidable c) {
        collidables.add(c);
    }

    @Override
    public void finalize() {

    }
}
