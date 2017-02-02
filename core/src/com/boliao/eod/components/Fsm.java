package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 19/1/17.
 * Finite State Machine class.
 */

public abstract class Fsm extends Component {
    public static final String TAG = "FSM:C";

    // todo: change SEEK to ARRIVE
    public enum StateType {IDLE, MOVE, PURSUE, COLLISION_RESPONSE, ATTACK, BUILD, DESTRUCT};
    protected StateType currState = StateType.IDLE;

    protected Transform transform;
    protected Collider collider;
    protected Movement movement;
    protected Steering steering;
    protected SpriteSheet spriteSheet;

    public Fsm () {
        super("Fsm");
    }

    public Fsm (String name) {
        super(name);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");
        collider = (Collider) owner.getComponent("Collider");
        spriteSheet = (SpriteSheet) owner.getComponent("SpriteSheet");
        movement = (Movement) owner.getComponent("Movement");

        //todo: need to assert all components not null
    }

    @Override
    public abstract void update(float delta);
}
