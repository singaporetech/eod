package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 23/1/17.
 */

public class FsmPlayer extends Fsm {
    private static final String TAG = "FsmPlayer:C";

    private Input input;

    public FsmPlayer() {
        super("FsmPlayer");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup additional links
        steering = (Steering) owner.getComponent("SteeringArriveCollision");
        input = (Input) owner.getComponent("Input");
    }

    @Override
    public void update(float delta) {
        switch (currState) {
            case IDLE:
                // if health=0, go to DESTRUCT

                // do transitions
                if (input.isTriggered()) {

                    steering.setDestPos(input.getWorldPos2D());

                    // transit
                    currState = StateType.SEEK;
                    spriteSheet.enter(); // todo: this should be currState.enter() when State class setup
                    Gdx.app.log(TAG, "TOUCHED condition; Transit to SEEK destPos=" + steering.getDestPos());
                }

                break;

            case SEEK:
                // do transitions
                if (steering.reachedDestPos()) {
                    // transit
                    spriteSheet.exit();
                    currState = StateType.IDLE;
                    Gdx.app.log(TAG, "Transit to IDLE");
                }
                if (input.isJustTriggered()) {
                    steering.setDestPos(input.getWorldPos2D());

                    Gdx.app.log(TAG, "TOUCHED condition; Stay in SEEK destPos=" + steering.getDestPos());
                }

                // do actions
                movement.move(delta, steering.getForce());
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
