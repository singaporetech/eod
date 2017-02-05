package com.boliao.eod.components.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.Game;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.components.*;
import com.boliao.eod.components.render.SpriteSheet;

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
        spriteSheet = (SpriteSheet) owner.getComponent("SpriteSheetPlayer");
        steering = (Steering) owner.getComponent("SteeringArrive");
        input = (Input) owner.getComponent("Input");
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        Vector2 avoidTarget;

        // do transitions
        switch (currState) {
            case IDLE:
                // if health=0, go to DESTRUCT
                if (health.isEmpty()) {
                    transit(StateType.DESTRUCT);
                }

                if (input.isTriggered()) {
                    Gdx.app.log(TAG, "MOUSE PICKED");
                    lastDestPos.set(input.getWorldPos2D()); // update destination position
                    transit(StateType.MOVE);
                }
                break;

            case MOVE:
                avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (avoidTarget != null) {
                    Gdx.app.log(TAG, "COLLISION DETECTED");
                    transit(StateType.COLLISION_RESPONSE);
                }
                else if (steering.reachedDestPos()) {
                    Gdx.app.log(TAG, "REACHED DEST POS");
                    transit(StateType.IDLE);
                }
                else if (input.isJustTriggered()) {
                    Gdx.app.log(TAG, "NEW MOUSE PICKED; stay in MOVE");
                    lastDestPos.set(input.getWorldPos2D());
                    steering.setDestPos(lastDestPos);
                }
                break;

            case COLLISION_RESPONSE:
                avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                if (avoidTarget == null) {
                    Gdx.app.log(TAG, "NO MORE COLLISIONS");
                    transit(StateType.MOVE);
                }
                else if (steering.reachedDestPos()) {
                    Gdx.app.log(TAG, "REACHED COLLISION AVOID TARGET");
                    transit(StateType.MOVE);
                 }
                break;

            case BUILD:
                break;

            case DESTRUCT:
                break;

            default:
                break;
        }

    }

}
