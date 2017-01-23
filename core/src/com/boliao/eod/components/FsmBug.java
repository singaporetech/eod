package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 23/1/17.
 */

public class FsmBug extends Fsm {
    private static final String TAG = "FsmBug:C";

    public FsmBug() {
        super("FsmBug");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup additional links
        steering = (Steering) owner.getComponent("SteeringPursue");
    }

    @Override
    public void update(float delta) {
        switch (currState) {
            case IDLE:
                currState = StateType.PURSUE;
                Gdx.app.log(TAG, "Transit to PURSUE");
                break;

            case PURSUE:
                // do transitions
                if (steering.reachedDestPos()) {
                    // transit
                    currState = StateType.ATTACK;
                    Gdx.app.log(TAG, "Transit to ATTACK");
                }

                // do actions
                movement.move(delta, steering.getForce());
                break;

            case ATTACK:
                // do transitions
                if (((SteeringPursue)steering).targetGotAway()) {
                    currState = StateType.PURSUE;
                    Gdx.app.log(TAG, "Transit to PURSUE");
                }

                // do actions
                // todo: attack animations
                break;

            case BUILD:
                break;

            case DESTRUCT:
                Gdx.app.log(TAG, "Destroying");
                break;

            default:
                break;
        }
    }
}
