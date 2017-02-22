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

    private GameObject pickedBug = null;

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
    public void update(float dt) {
        super.update(dt);

        // do fade out animation for sprite
        if (spriteInput.getAlpha() > 0) {
            spriteInput.shrinkAndFade(SETTINGS.X_FADEOUT_DECREMENT, dt);
        }
    }

    public boolean isTriggered() {
        switch (type) {
            case TOUCH:
                if (Gdx.input.isTouched()) {
                    processPos(Gdx.input.getX(), Gdx.input.getY());
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isJustTriggered() {
        switch (type) {
            case TOUCH:
                if (Gdx.input.justTouched()) {
                    processPos(Gdx.input.getX(), Gdx.input.getY());
                    return true;
                }
                break;
        }
        return false;
    }

    private void processPos(int x, int y) {
        // set all pos
        screenPos3D.set(x, y, 0);
        worldPos3D.set(screenPos3D);
        RenderEngine.i().getCam().unproject(worldPos3D);
        worldPos2D.set(worldPos3D.x, worldPos3D.y);

        // get collided game object with this position
        pickedBug = CollisionEngine.i().getObjectCollidedWithPos(worldPos2D);

        // check if it's a walkable position
        if (pickedBug == null) {
            spriteInput.reset();
            spriteInput.setPos(worldPos2D);
        }

        // check whether it's actually a bug that's picked
        else if (!pickedBug.getName().contains("bug")) {
            pickedBug = null;
        }
    }

    public GameObject getPickedBug() {
        return pickedBug;
    }

    public Vector2 getWorldPos2D() {
        return worldPos2D;
    }
}
