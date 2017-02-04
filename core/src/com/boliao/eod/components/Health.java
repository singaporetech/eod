package com.boliao.eod.components;

import com.boliao.eod.GameObject;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.render.SpriteHealth;

/**
 * Created by mrboliao on 2/2/17.
 */

public class Health extends Component {
    private static final String TAG = "Health:C";

    protected Transform transform;
    protected SpriteHealth primitiveHealth;

    protected float maxHp = SETTINGS.PLAYER_HP;
    protected float hp = maxHp;

    public Health() {
        super("Health");
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        transform = (Transform) owner.getComponent("Transform");
        primitiveHealth = (SpriteHealth) owner.getComponent("SpriteHealth");
    }

    public void hit(float dmg) {
        hp -= dmg;
        if (hp < 0) {
            hp = 0;
        }
        primitiveHealth.scaleWidth(hp/maxHp);
    }

    public void heal(float amt) {
        hp += amt;
        if (hp > maxHp) {
            hp = maxHp;
        }
        primitiveHealth.scaleWidth(hp/maxHp);
    }

    public boolean isEmpty() {
        return (hp == 0);
    }
}
