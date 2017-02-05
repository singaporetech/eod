package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.render.SpriteInput;

/**
 * Created by mrboliao on 20/1/17.
 */

public class Input extends Component{
    private static final String TAG = "Input:Component";

    private SpriteInput spriteInput;

    public enum InputType {TOUCH}
    private InputType type;

    private Vector3 screenPos3D = new Vector3();
    private Vector3 worldPos3D = new Vector3();
    private Vector2 worldPos2D = new Vector2();

    public Input(InputType type) {
        super("Input");

        this.type = type;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        spriteInput = (SpriteInput) owner.getComponent("SpriteInput");
        spriteInput.setAlpha(0);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // do fade out animation for sprite
        if (spriteInput.getAlpha() > 0) {
            spriteInput.shrinkAndFade(SETTINGS.X_FADEOUT_DECREMENT, delta);
        }
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
            spriteInput.reset();
            spriteInput.setPos(worldPos2D);
            return true;
        }
        return false;
    }

    public Vector2 getWorldPos2D() {
        return worldPos2D;
    }
}
