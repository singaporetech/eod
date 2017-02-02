package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 20/1/17.
 */

public class Input extends Component implements Renderable {
    private static final String TAG = "Input:Component";

    public enum InputType {TOUCH}
    private InputType type;

    private Vector3 screenPos3D = new Vector3();
    private Vector3 worldPos3D = new Vector3();
    private Vector2 worldPos2D = new Vector2();

    private com.badlogic.gdx.graphics.g2d.Sprite sprite;
    private float spriteAlpha = 1;

    public Input(InputType type) {
        super("Input");

        this.type = type;

        // init texture
        sprite = new com.badlogic.gdx.graphics.g2d.Sprite(new Texture(SETTINGS.X_SPRITEPATH));
        sprite.setOriginCenter();
        sprite.setSize(SETTINGS.X_SIZE, SETTINGS.X_SIZE);
        spriteAlpha = 0;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        RenderEngine.i().addRenderable(this);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // do fade out animation for sprite
        if (spriteAlpha > 0) {
            spriteAlpha -= SETTINGS.X_FADEOUT_DECREMENT * delta;
        }
        if (spriteAlpha < 0) {
            spriteAlpha = 0;
        }
        sprite.setAlpha(spriteAlpha);
    }

    @Override
    public void draw() {
        sprite.draw(RenderEngine.i().getSpriteBatch());
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }

    public boolean isTriggered() {
        switch (type) {
            case TOUCH:
                if (Gdx.input.isTouched()) {
                    return processPos(Gdx.input.getX(), Gdx.input.getY());
                }
                break;
        }
        return false;
    }

    public boolean isJustTriggered() {
        switch (type) {
            case TOUCH:
                if (Gdx.input.justTouched()) {
                    return processPos(Gdx.input.getX(), Gdx.input.getY());
                }
                break;
        }
        return false;
    }

    private boolean processPos(int x, int y) {
        // set all pos
        screenPos3D.set(x, y, 0);
        worldPos3D.set(screenPos3D);
        RenderEngine.i().getCam().unproject(worldPos3D);
        worldPos2D.set(worldPos3D.x, worldPos3D.y);

        // check collisions and set visuals
        if (CollisionEngine.i().isFreeOfCollisions(worldPos2D)) {
            spriteAlpha = 1;
            sprite.setPosition(worldPos2D.x, worldPos2D.y);
            return true;
        }
        return false;
    }

    public Vector2 getWorldPos2D() {
        return worldPos2D;
    }
}
