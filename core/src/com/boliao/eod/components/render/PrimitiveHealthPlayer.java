package com.boliao.eod.components.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 6/2/17.
 */

public class PrimitiveHealthPlayer extends PrimitiveHealth {
    public PrimitiveHealthPlayer() {
        // set to fixed top of screen
        maxWidth = SETTINGS.HEALTHBAR_PLAYER_WIDTH;
        width = maxWidth/2;
        height = SETTINGS.HEALTHBAR_PLAYER_HEIGHT;
    }

    @Override
    public Vector2 getRightEdgePos() {
        return new Vector2(SETTINGS.HEALTHBAR_PLAYER_X + width + SETTINGS.PLUSONE_OFFSET_X, SETTINGS.HEALTHBAR_PLAYER_Y + SETTINGS.PLUSONE_OFFSET_Y);
    }

    @Override
    public void draw() {
        // fill
        Gdx.gl.glEnable(GL30.GL_BLEND);
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Filled);
        RenderEngine.i().getDebugRenderer().setColor(0.7f,0f,0f,0.8f);
        RenderEngine.i().getDebugRenderer().rect(SETTINGS.HEALTHBAR_PLAYER_X, SETTINGS.HEALTHBAR_PLAYER_Y, width, height);
        RenderEngine.i().getDebugRenderer().end();
        Gdx.gl.glDisable(GL30.GL_BLEND);

        // outline
        Gdx.gl.glLineWidth(SETTINGS.HEALTHBAR_LINEWIDTH);
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Line);
        RenderEngine.i().getDebugRenderer().setColor(0.7f,0.5f,0.5f,0.7f);
        RenderEngine.i().getDebugRenderer().rect(SETTINGS.HEALTHBAR_PLAYER_X, SETTINGS.HEALTHBAR_PLAYER_Y, maxWidth, height);
        RenderEngine.i().getDebugRenderer().end();
    }
}
