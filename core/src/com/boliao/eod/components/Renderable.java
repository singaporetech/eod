package com.boliao.eod.components;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by mrboliao on 19/1/17.
 */

public interface Renderable {
    void draw();
    Rectangle getBoundingBox();
}
