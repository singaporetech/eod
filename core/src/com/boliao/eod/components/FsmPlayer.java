package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 23/1/17.
 */

public class FsmPlayer extends Fsm {
    private static final String TAG = "FsmPlayer:C";

    private Input input;
    private Steering steeringCollision;

    // to remember last destination position to resume steering after collision
    private Vector2 lastDestPos = new Vector2();

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
        Vector2 target;

        switch (currState) {
            case IDLE:
                // if health=0, go to DESTRUCT

                // do transitions
                if (input.isTriggered()) {

                    lastDestPos = input.getWorldPos2D();

                    // transit
                    enterMove(); // todo: this should be currState.enter() when State class setup
                    currState = StateType.MOVE;
                    spriteSheet.enter(); // todo: this should be currState.enter() when State class setup
                    Gdx.app.log(TAG, "TOUCHED condition; Transit to SEEK destPos=" + steering.getDestPos());
                }

                break;

            case MOVE:
                // do transitions
                if (steering.reachedDestPos()) {
                    // transit
                    spriteSheet.exit();
                    currState = StateType.IDLE;

                    Gdx.app.log(TAG, "REACHED_DEST; Transit to IDLE");
                }

                target = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (target != null) {
                    steering.setDestPos(target);

                    spriteSheet.exit();
                    currState = StateType.COLLISION_RESPONSE;

                    Gdx.app.log(TAG, "COLLIDED condition; Transit to COLLISION_RESPONSE destPos=" + steering.getDestPos());
                }
                else if (input.isJustTriggered()) {
                    lastDestPos = input.getWorldPos2D();
                    steering.setDestPos(lastDestPos);

                    Gdx.app.log(TAG, "TOUCHED condition; Stay in MOVE destPos=" + steering.getDestPos());
                }

                // do actions
                movement.move(delta, steering.getForce());
                break;

            case COLLISION_RESPONSE:
                // do transitions
                if (steering.reachedDestPos()) {
                    // transit
                    spriteSheet.exit();
                    enterMove();
                    currState = StateType.MOVE;

                    Gdx.app.log(TAG, "REACHED_COLLISION_RESPONSE_TARGET; Transit to MOVE");
                }

                target = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (target == null) {
                    // transit
                    enterMove();
                    currState = StateType.MOVE;
                    spriteSheet.enter();
                    Gdx.app.log(TAG, "NO_COLLISIONS condition; Transit to MOVE destPos=" + steering.getDestPos());
                }

                // do actions
                // set steering target to off-object position and seek
                movement.move(delta, steering.getBaseForce());
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

    private void enterMove() {
        steering.setDestPos(lastDestPos);
    }
    private void exitMove() {
        lastDestPos = steering.getDestPos();
    }
}
