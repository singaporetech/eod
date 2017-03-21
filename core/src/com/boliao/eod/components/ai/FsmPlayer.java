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
    private SteeringPursue steeringPursue;
    private SteeringArrive steeringArrive;

    public FsmPlayer() {
        super("FsmPlayer");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup additional links
        spriteSheet = (SpriteSheet) owner.getComponent("SpriteSheetPlayer");
        steeringArrive = (SteeringArrive) owner.getComponent("SteeringArrive");
        steeringPursue = (SteeringPursue) owner.getComponent("SteeringPursue");
        steering = steeringArrive;
        input = (Input) owner.getComponent("Input");
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        Vector2 avoidTarget;

        // do transitions
        switch (currState) {
            case IDLE:
                if (health.isEmpty()) {
                    transit(StateType.DESTRUCT);
                }
                else {
                    if (input.isTriggered()) {
                        Gdx.app.log(TAG, "MOUSE PICKED");

                        // check whether clicked on Bug
                        if (input.getPickedBug() != null) {
                            Gdx.app.log(TAG, "NEW TARGET BUG PICKED: " + input.getPickedBug().getName());
                            // todo: see how to generalize this
                            steeringPursue.setTarget(input.getPickedBug());
                            steering = steeringPursue;
                            transit(StateType.PURSUE);
                        } else {
                            Gdx.app.log(TAG, "NEW MOVE POS PICKED: " + input.getWorldPos2D());
                            lastDestPos.set(input.getWorldPos2D()); // update destination position
                            steering = steeringArrive;
                            transit(StateType.MOVE);
                        }
                    }
                }
                break;

            case MOVE:
                if (health.isEmpty()) {
                    transit(StateType.DESTRUCT);
                }
                else {
                    avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                    if (avoidTarget != null) {
                        Gdx.app.log(TAG, "COLLISION DETECTED");
                        transit(StateType.COLLISION_RESPONSE);
                    } else if (steering.reachedDestPos()) {
                        Gdx.app.log(TAG, "REACHED DEST POS");
                        transit(StateType.IDLE);
                    } else if (input.isJustTriggered()) {
                        // check whether clicked on Bug
                        if (input.getPickedBug() != null) {
                            Gdx.app.log(TAG, "NEW TARGET BUG PICKED");

                            // todo: see how to generalize this
                            steeringPursue.setTarget(input.getPickedBug());
                            steering = steeringPursue;
                            transit(StateType.PURSUE);
                        } else {
                            Gdx.app.log(TAG, "NEW MOVE POS PICKED");
                            lastDestPos.set(input.getWorldPos2D());
                            steering.setDestPos(lastDestPos);
                        }
                    }
                }
                break;

            case PURSUE:
                // todo: allow clicking anywhere to transit move

                if (health.isEmpty()) {
                    transit(StateType.DESTRUCT);
                }
                else {
                    avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                    if (avoidTarget != null) {
                        Gdx.app.log(TAG, "COLLISION DETECTED");
                        transit(StateType.COLLISION_RESPONSE);
                    } else if (steering.reachedDestPos()) {
                        Gdx.app.log(TAG, "REACHED TARGET");
                        combat.setTarget(input.getPickedBug());
                        transit(StateType.ATTACK);
                    } else if (input.isTriggered()) {
                        Gdx.app.log(TAG, "MOUSE PICKED");

                        // check whether clicked on Bug
                        if (input.getPickedBug() != null) {
                            Gdx.app.log(TAG, "NEW TARGET BUG PICKED");
                            // todo: see how to generalize this
                            steeringPursue.setTarget(input.getPickedBug());
                            steering = steeringPursue;
                            transit(StateType.PURSUE);
                        } else {
                            Gdx.app.log(TAG, "NEW MOVE POS PICKED");
                            lastDestPos.set(input.getWorldPos2D()); // update destination position
                            steering = steeringArrive;
                            transit(StateType.MOVE);
                        }
                    }
                }
                break;

            case COLLISION_RESPONSE:
                if (health.isEmpty()) {
                    transit(StateType.DESTRUCT);
                }
                else {
                    avoidTarget = CollisionEngine.i().getCollisionAvoidTarget(collider);
                    if (avoidTarget == null) {
                        Gdx.app.log(TAG, "NO MORE COLLISIONS");
                        steering = steeringArrive;
                        transit(StateType.MOVE);
                    } else if (steering.reachedDestPos()) {
                        Gdx.app.log(TAG, "REACHED COLLISION AVOID TARGET");
                        steering = steeringArrive;
                        transit(StateType.MOVE);
                    }
                }
                break;

            case ATTACK:
                if (health.isEmpty()) {
                    transit(StateType.DESTRUCT);
                }
                else {
                    // todo: allow clicking anywhere to transit move
                    if (input.isTriggered()) {
                        Gdx.app.log(TAG, "MOUSE PICKED");

                        // check whether clicked on Bug
                        if (input.getPickedBug() != null) {
                            Gdx.app.log(TAG, "NEW TARGET BUG PICKED");
                            // todo: see how to generalize this
                            steeringPursue.setTarget(input.getPickedBug());
                            steering = steeringPursue;
                            transit(StateType.PURSUE);
                        } else {
                            Gdx.app.log(TAG, "NEW MOVE POS PICKED");
                            lastDestPos.set(input.getWorldPos2D()); // update destination position
                            steering = steeringArrive;
                            transit(StateType.MOVE);
                        }
                    }
                }
                break;

            case BUILD:
                break;

            case DESTRUCT:
                // game over
                Game.i().pause();
                RenderEngine.i().showEndGameMenu();
                break;

            default:
                break;
        }

    }
}
