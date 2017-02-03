package com.boliao.eod.components.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 3/2/17.
 */

public class PrimitiveHealth extends Primitive {
    protected float maxWidth = SETTINGS.HEALTHBAR_WIDTH;
    protected float height = SETTINGS.HEALTHBAR_HEIGHT;
    protected float width = maxWidth;

    public PrimitiveHealth() {
        super("PrimitiveHealth");
    }

    public void scaleWidth(float scale) {
        width = scale * maxWidth;
        if (width < 0) {
            width = 0;
        }
    }

    @Override
    public void draw() {
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Filled);
        RenderEngine.i().getDebugRenderer().setColor(1,0,0,0);
        RenderEngine.i().getDebugRenderer().rect(transform.getX()+ SETTINGS.HEALTHBAR_OFFSET_X, transform.getY()+SETTINGS.HEALTHBAR_OFFSET_Y, width, height);
        RenderEngine.i().getDebugRenderer().end();
    }
}
