package com.boliao.eod.components.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.Game;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.Combat;
import com.boliao.eod.components.Health;
import com.boliao.eod.components.collision.Collider;
import com.boliao.eod.components.Component;
import com.boliao.eod.components.Movement;
import com.boliao.eod.components.render.SpriteBam;
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
    protected SpriteBam spriteBam;
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

    /**
     * Note that some component links need to be set in the subclass
     * @param owner
     */
    @Override
    public void init(GameObject owner) {
        super.init(owner);
        TAG += ":" + owner.getName();

        transform = (Transform) owner.getComponent("Transform");
        collider = (Collider) owner.getComponent("Collider");
        movement = (Movement) owner.getComponent("Movement");
        combat = (Combat) owner.getComponent("Combat");
        health = (Health) owner.getComponent("Health");
        spriteBam = (SpriteBam) owner.getComponent("SpriteBam");
        //todo: need to assert all components not null
    }

    @Override
    public void update(float dt) {
        // act on current state
        actCurrState(dt);
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
                spriteSheet.onAnimation(SpriteSheet.Sequence.RUN);
                break;
            case PURSUE: // todo: exact same as MOVE now
                steering.setDestPos(lastDestPos);
                spriteSheet.onAnimation(SpriteSheet.Sequence.RUN);
                break;
            case COLLISION_RESPONSE:
                steering.setDestPos(collider.getCollisionAvoidTarget());
                spriteSheet.onAnimation(SpriteSheet.Sequence.RUN);
                break;
            case ATTACK:
                movement.faceTargetPos(combat.getTargetPos());
                combat.enable();
                spriteSheet.onAnimation(SpriteSheet.Sequence.MELEE);
                break;
            case DESTRUCT:
                spriteSheet.onAnimation(SpriteSheet.Sequence.DESTRUCT, false);
                spriteSheet.setAlpha(SETTINGS.DESTRUCTED_ALPHA);
                owner.setDestroyed();
                //todo: destroy object on finish animation
                break;
            case BUILD:
                break;
            default:
                break;
        }
    }

    protected void actCurrState(float dt) {
        switch(currState) {
            case IDLE:
                break;
            case MOVE:
                movement.move(dt, steering.getForce());
                break;
            case PURSUE:
                movement.move(dt, steering.getForce());
                break;
            case COLLISION_RESPONSE:
                // set steering target to off-object position and seek
                movement.move(dt, steering.getBaseForce());
                break;
            case ATTACK:
                if (combat.isTargetDestroyed()) {
                    combat.releaseTarget();
                    transit(StateType.IDLE);
                }
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
                spriteBam.disable();
                spriteSheet.offAnimation();
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
