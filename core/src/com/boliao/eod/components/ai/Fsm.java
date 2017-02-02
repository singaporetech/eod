package com.boliao.eod.components.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.components.Collider;
import com.boliao.eod.components.Component;
import com.boliao.eod.components.Movement;
import com.boliao.eod.components.SpriteSheet;
import com.boliao.eod.components.Steering;
import com.boliao.eod.components.Transform;

/**
 * Created by mrboliao on 19/1/17.
 * Finite State Machine class.
 */

public abstract class Fsm extends Component {
    private String TAG = "FSM:C";

    // todo: change SEEK to ARRIVE
    public enum StateType {IDLE, MOVE, PURSUE, COLLISION_RESPONSE, ATTACK, BUILD, DESTRUCT};
    protected StateType currState = StateType.IDLE;

    protected Transform transform;
    protected Collider collider;
    protected Movement movement;
    protected Steering steering;
    protected SpriteSheet spriteSheet;

    // to remember last destination position to resume steering after collision
    protected Vector2 lastDestPos = new Vector2();

    public Fsm () {
        super("Fsm");
    }

    public Fsm (String name) {
        super(name);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);
        TAG += ":" + owner.getName();

        transform = (Transform) owner.getComponent("Transform");
        collider = (Collider) owner.getComponent("Collider");
        spriteSheet = (SpriteSheet) owner.getComponent("SpriteSheet");
        movement = (Movement) owner.getComponent("Movement");

        //todo: need to assert all components not null
    }

    @Override
    public abstract void update(float delta);

    protected void transit(StateType targetState) {
        currState = targetState;
        Gdx.app.log(TAG, "Transit to " + targetState + " lastDestPos=" + lastDestPos);
    }

    protected void enterMove() {
        steering.setDestPos(lastDestPos);
        spriteSheet.onAnimation();
    }
    protected void actMove(float delta) {
        movement.move(delta, steering.getForce());
    }
    protected void exitMove() {
        lastDestPos.set(steering.getDestPos());
        spriteSheet.offAnimation();
    }

    protected void enterCollisionResponse(Vector2 avoidTarget) {
        steering.setDestPos(avoidTarget);
        spriteSheet.onAnimation();
    }
    protected void actCollisionResponse(float delta) {
        // set steering target to off-object position and seek
        movement.move(delta, steering.getBaseForce());

    }
    protected void exitCollisionResponse() {
        spriteSheet.offAnimation();
    }

    protected void enterPursue() {
        enterMove();
    }
    protected void actPursue(float delta) {
        actMove(delta);
    }
    protected void exitPursue() {
        exitMove();
    }

    protected void enterAttack() {

    }
    protected void actAttack(float delta) {

    }
    protected void exitAttack() {

    }

    protected void enterIdle() {

    }
    protected void actIdle(float delta) {

    }
    protected void exitIdle() {

    }
}
