package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;

/**
 * Created by mrboliao on 19/1/17.
 * Finite State Machine class.
 */

public class Fsm extends Component {
    public static final String TAG = "FSM_COMPONENT";

    public enum StateType {IDLE, SEEK, BUILD, DESTRUCT};
    protected StateType currState = StateType.IDLE;

    private Transform transform;
    private Movement movement;

    public Fsm () {
        super("Fsm");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");
        movement = (Movement) owner.getComponent("Movement");
    }

    @Override
    public void update(float delta) {
        switch (currState) {
            case IDLE:
                // if health=0, go to DESTRUCT

                // do transitions
                if (Gdx.input.isTouched()) {
                    Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                    RenderEngine.i().getCam().unproject(touchPos);
                    movement.setDestPos(touchPos.x, touchPos.y);

                    currState = StateType.SEEK;

                    Gdx.app.log(TAG, "Transit to SEEK");
                }

                break;

            case SEEK:
                // do transitions
                if (movement.reachedDestPos()) {
                    currState = StateType.IDLE;

                    Gdx.app.log(TAG, "Transit to IDLE");
                }
//                if (Gdx.input.isTouched()) {
//                    movement.setDestPos(Gdx.input.getX(), Gdx.input.getY());
//
//                    Gdx.app.log(TAG, "reset destPos x=" + Gdx.input.getX() + " y=" + Gdx.input.getY());
//                }

                // do actions
                movement.moveInDir(delta);
                break;

            case DESTRUCT:
                Gdx.app.log(TAG, "Destroying");
                break;

            default:
                break;
        }
    }
}
