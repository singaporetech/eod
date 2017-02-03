package com.boliao.eod.components.render;

import com.badlogic.gdx.math.Rectangle;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.Component;
import com.boliao.eod.components.Transform;

/**
 * Created by mrboliao on 3/2/17.
 */

public abstract class Primitive extends Component implements RenderableDebug {
    protected Transform transform;

    public Primitive(String name) {
        super(name);
    }

    public Primitive() {
        super("Primitive");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");

        RenderEngine.i().addRenderableDebug(this);
    }

    @Override
    public abstract void draw();

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }
}
