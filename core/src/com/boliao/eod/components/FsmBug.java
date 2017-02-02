package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
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
        Vector2 avoidTarget;

        switch (currState) {
            case IDLE:
                Gdx.app.log(TAG, "SKIP IDLE");
                exitIdle();
                transit(StateType.PURSUE);
                break;

            //todo: refactor: this is currently exactly the same as MOVE
            case PURSUE:
                // do actions
                actPursue(delta);

                // do transitions
                // todo: conditions to be a class for reuse
                avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (avoidTarget != null) {
                    Gdx.app.log(TAG, "COLLISION DETECTED");

                    exitPursue();
                    transit(StateType.COLLISION_RESPONSE);
                    enterCollisionResponse(avoidTarget);
                }

                else if (steering.reachedDestPos()) {
                    Gdx.app.log(TAG, "REACHED TARGET");

                    exitPursue();
                    transit(StateType.ATTACK);
                    enterAttack();
                }
                break;

            case COLLISION_RESPONSE:
                // do actions
                actCollisionResponse(delta);

                // NO_MORE_COLLISIONS; transit to MOVE
                avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (avoidTarget == null) {
                    Gdx.app.log(TAG, "NO MORE COLLISIONS");

                    exitCollisionResponse();
                    transit(StateType.PURSUE);
                    enterPursue();
                }

                // REACHED_DEST; transit to MOVE
                else if (steering.reachedDestPos()) {
                    Gdx.app.log(TAG, "REACHED COLLISION AVOID TARGET");

                    exitCollisionResponse();
                    transit(StateType.PURSUE);
                    enterPursue();
                }
                break;

            case ATTACK:
                // do actions
                actAttack(delta);

                // do transitions
                if (((SteeringPursue)steering).targetGotAway()) {
                    Gdx.app.log(TAG, "ATTACK TARGET GOT AWAY");

                    exitAttack();
                    transit(StateType.PURSUE);
                    enterPursue();
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
