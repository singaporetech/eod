package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Controller extends Component {
    private static final String TAG = "Controller:Component";

    private Transform transform;

    private Vector2 destPos;

    public Controller() {
        super("Controller");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");

        // init dest pos
        destPos = new Vector2(transform.getX(), transform.getY());
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isTouched()) {
            destPos.set(Gdx.input.getX(), Gdx.input.getY());
            transform.translate(SETTINGS.HUMAN_SPEED*delta, 0);
            Gdx.app.log(TAG, "touched");
        }
    }
}
