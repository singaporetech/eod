package com.boliao.eod.components;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by mrboliao on 24/1/17.
 */

public interface Collidable {
//    boolean hasCollidedWithLine(Vector2 pos1, Vector2 pos2);
    Vector2 getBoundingCirclePos();
    float getBoundingCircleRadius();
    Vector2 getCollisionNorm(Collidable collider);
//    Vector2 getCollisionDir(Vector2 forwardPos);
//    Polygon getBoundingPolygon();
}
