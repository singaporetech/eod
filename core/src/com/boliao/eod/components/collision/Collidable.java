package com.boliao.eod.components.collision;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 24/1/17.
 */

public interface Collidable {
    boolean isStatic();
    boolean isCollidable();
    GameObject getOwner();
    float getBoundingCircleRadius();
    Vector2 getBoundingCirclePos();
    Vector2 getCollisionNorm(Collidable other);
    boolean collidedWithPos(Vector2 pos);
}
