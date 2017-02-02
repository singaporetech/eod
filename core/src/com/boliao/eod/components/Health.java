package com.boliao.eod.components;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 2/2/17.
 */

public class Health extends Component implements RenderableDebug {
    private static final String TAG = "Health:C";

    protected Transform transform;

    protected float maxWidth = SETTINGS.HEALTHBAR_WIDTH;
    protected float height = SETTINGS.HEALTHBAR_HEIGHT;
    protected float width = maxWidth;

    public Health() {
        super("Health");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");

        RenderEngine.i().addRenderableDebug(this);
    }

    private void scaleWidth(float scale) {
        width = scale * maxWidth;
        if (width < 0) {
            width = 0;
        }
    }

    @Override
    public void draw() {
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Filled);
        RenderEngine.i().getDebugRenderer().setColor(1,0,0,0);
        RenderEngine.i().getDebugRenderer().rect(transform.getX()+SETTINGS.HEALTHBAR_OFFSET_X, transform.getY()+SETTINGS.HEALTHBAR_OFFSET_Y, width, height);
        RenderEngine.i().getDebugRenderer().end();
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }
}
