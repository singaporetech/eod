package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;

/**
 * Created by mrboliao on 20/1/17.
 */

public class Input extends Component {
    private static final String TAG = "Input:Component";

    public enum InputType {TOUCH}
    private InputType type;

    private Vector3 screenPos3D = new Vector3();
    private Vector3 worldPos3D = new Vector3();

    public Input(InputType type) {
        super("Input");

        this.type = type;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);
    }

    public boolean isTriggered() {
        switch (type) {
            case TOUCH:
                if (Gdx.input.isTouched()) {
                    setScreenPos3D();
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
                    setScreenPos3D();
                    return true;
                }
                break;
        }
        return false;
    }

    private void setScreenPos3D() {
        screenPos3D.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldPos3D.set(screenPos3D);
        RenderEngine.i().getCam().unproject(worldPos3D);
    }

    public Vector2 getWorldPos2D() {
        return new Vector2(worldPos3D.x, worldPos3D.y);
    }
}
