package com.boliao.eod.components.render;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by mrboliao on 19/1/17.
 */

/**
 * For each class that implements Renderable:
 * 1. in init(), add self to Renderables list in RenderEngine
 * 2. implement draw
 */
public interface Renderable {
    void draw();
    Rectangle getBoundingBox();
}
