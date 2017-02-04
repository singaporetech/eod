package com.boliao.eod.components.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.Game;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.components.Combat;
import com.boliao.eod.components.Health;
import com.boliao.eod.components.collision.Collider;
import com.boliao.eod.components.Component;
import com.boliao.eod.components.Movement;
import com.boliao.eod.components.render.SpriteSheet;
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
    protected Combat combat;
    protected Health health;

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
        combat = (Combat) owner.getComponent("Combat");
        health = (Health) owner.getComponent("Health");
        //todo: need to assert all components not null
    }

    @Override
    public void update(float delta) {
        // act on current state
        actCurrState(delta);
    }

    protected void transit(StateType targetState) {
        String logStr = "Transit from " + currState + " to ";

        exitCurrState();
        currState = targetState;
        enterCurrState();

        Gdx.app.log(TAG, logStr + currState);
    }

    protected void enterCurrState() {
        switch(currState) {
            case IDLE:
                break;
            case MOVE:
                steering.setDestPos(lastDestPos);
                spriteSheet.onAnimation();
                break;
            case PURSUE: // todo: exact same as MOVE now
                steering.setDestPos(lastDestPos);
                spriteSheet.onAnimation();
                break;
            case COLLISION_RESPONSE:
                steering.setDestPos(collider.getCollisionAvoidTarget());
                spriteSheet.onAnimation();
                break;
            case ATTACK:
                combat.enable();
                break;
            case DESTRUCT:
                Game.i().pause();
                RenderEngine.i().showEndGameMenu();
                break;
            case BUILD:
                break;
            default:
                break;
        }
    }

    protected void actCurrState(float delta) {
        switch(currState) {
            case IDLE:
                break;
            case MOVE:
                movement.move(delta, steering.getForce());
                break;
            case PURSUE:
                movement.move(delta, steering.getForce());
                break;
            case COLLISION_RESPONSE:
                // set steering target to off-object position and seek
                movement.move(delta, steering.getBaseForce());
                break;
            case ATTACK:
                break;
            case DESTRUCT:
                break;
            case BUILD:
                break;
            default:
                break;
        }
    }

    protected void exitCurrState() {
        switch(currState) {
            case IDLE:
                break;
            case MOVE:
                lastDestPos.set(steering.getDestPos());
                spriteSheet.offAnimation();
                break;
            case PURSUE:
                lastDestPos.set(steering.getDestPos());
                spriteSheet.offAnimation();
                break;
            case COLLISION_RESPONSE:
                spriteSheet.offAnimation();
                break;
            case ATTACK:
                combat.disable();
                break;
            case DESTRUCT:
                break;
            case BUILD:
                break;
            default:
                break;
        }
    }
}
