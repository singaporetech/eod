package com.boliao.eod.components.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 6/2/17.
 */

public class PrimitiveHealth extends Primitive {
    protected float maxWidth = SETTINGS.HEALTHBAR_WIDTH;
    protected float width = maxWidth;
    protected float height = SETTINGS.HEALTHBAR_HEIGHT;

    public PrimitiveHealth() {
        super("PrimitiveHealth");
    }

    public void scaleWidth(float scale) {
        width = scale * maxWidth;
        if (width < 0) {
            width = 0;
        }
    }

    public Vector2 getRightEdgePos() {
        return new Vector2(transform.getX()+ SETTINGS.HEALTHBAR_OFFSET_X + width, transform.getY()+SETTINGS.HEALTHBAR_OFFSET_Y);
    }

    @Override
    public void draw() {
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Filled);
        RenderEngine.i().getDebugRenderer().setColor(1,0,0,0);
        RenderEngine.i().getDebugRenderer().rect(transform.getX()+ SETTINGS.HEALTHBAR_OFFSET_X, transform.getY()+SETTINGS.HEALTHBAR_OFFSET_Y, width, height);
        RenderEngine.i().getDebugRenderer().end();
    }
}
