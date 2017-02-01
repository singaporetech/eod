package com.boliao.eod.components;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by mrboliao on 24/1/17.
 */

public interface Collidable {
    boolean isStatic();
    float getBoundingCircleRadius();
    Vector2 getBoundingCirclePos();
    Vector2 getCollisionNorm(Collidable other);
    void checkCollisionAndRespond(Collidable other);
}
