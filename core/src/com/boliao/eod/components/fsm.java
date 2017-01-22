package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.boliao.eod.GameObject;

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
    private Steering steering;
    private Input input;

    public Fsm () {
        super("Fsm");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");
        movement = (Movement) owner.getComponent("Movement");
        steering = (Steering) owner.getComponent("SteeringSeek");
        input = (Input) owner.getComponent("Input");

        //todo: need to assert all components not null
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
                    Gdx.app.log(TAG, "TOUCHED condition; Transit to SEEK destPos=" + steering.getDestPos());
                }

                break;

            case SEEK:
                // do transitions
                if (steering.reachedDestPos()) {
                    // transit
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
