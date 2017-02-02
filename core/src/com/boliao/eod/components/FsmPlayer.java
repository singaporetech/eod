package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
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
        steering = (Steering) owner.getComponent("SteeringArrive");
        input = (Input) owner.getComponent("Input");
    }

    @Override
    public void update(float delta) {
        Vector2 avoidTarget;

        switch (currState) {
            case IDLE:
                // do actions
                actIdle(delta);

                // do transitions
                // if health=0, go to DESTRUCT

                if (input.isTriggered()) {
                    Gdx.app.log(TAG, "MOUSE PICKED");

                    // update destination position
                    lastDestPos.set(input.getWorldPos2D());

                    exitIdle(); //todo; this should be currState.exit()
                    transit(StateType.MOVE);
                    enterMove(); // todo: this should be currState.enter() when State class setup
                }
                break;

            case MOVE:
                // do actions
                actMove(delta);

                // do transitions
                if (steering.reachedDestPos()) {
                    Gdx.app.log(TAG, "REACHED DEST POS");

                    exitMove();
                    transit(StateType.IDLE);
                    enterIdle();
                }

                // COLLIDED condition; transit to COLLISION_RESPONSE
                avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (avoidTarget != null) {
                    Gdx.app.log(TAG, "COLLISION DETECTED");

                    exitMove();
                    transit(StateType.COLLISION_RESPONSE);
                    enterCollisionResponse(avoidTarget);
                }

                else if (input.isJustTriggered()) {
                    Gdx.app.log(TAG, "NEW MOUSE PICKED; stay in MOVE");

                    lastDestPos.set(input.getWorldPos2D());
                    steering.setDestPos(lastDestPos);
                }
                break;

            case COLLISION_RESPONSE:
                // do actions
                actCollisionResponse(delta);

                // do transitions®
                avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (avoidTarget == null) {
                    Gdx.app.log(TAG, "NO MORE COLLISIONS");

                    exitCollisionResponse();
                    transit(StateType.MOVE);
                    enterMove();
                }
                else if (steering.reachedDestPos()) {
                    Gdx.app.log(TAG, "REACHED COLLISION AVOID TARGET");

                    exitCollisionResponse();
                    transit(StateType.MOVE);
                    enterMove();
                 }
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
