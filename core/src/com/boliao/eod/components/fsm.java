package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Fsm extends Component {
    public static final String TAG = "FSM_COMPONENT";

    public enum StateType {IDLE, SEEK, BUILD, DESTRUCT};

    protected StateType currState = StateType.IDLE;

    public Fsm () {
        super("Fsm");
    }

    @Override
    public void update(float delta) {
        switch (currState) {
            case IDLE:
                // if health=0, go to DESTRUCT
                Gdx.app.log(TAG, "Idling...");
                break;

            case SEEK:
                Gdx.app.log(TAG, "Seeking...");
                break;

            case DESTRUCT:
                Gdx.app.log(TAG, "Destroying");
                break;

            default:
                break;
        }
    }
}
