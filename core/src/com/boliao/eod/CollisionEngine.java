package com.boliao.eod;

import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.components.Collidable;

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

    public Collidable getColliderWithLine(Vector2 pos1, Vector2 pos2) {
        for (Collidable c: collidables) {
            if (c.hasCollidedWithLine(pos1, pos2)) {
                return c;
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
