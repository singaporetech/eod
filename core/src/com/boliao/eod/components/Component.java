package com.boliao.eod.components;

import com.boliao.eod.GameObject;

/**
 * Created by mrboliao on 19/1/17.
 */

public abstract class Component {
    protected final String name;
    protected GameObject owner;
    protected boolean isActive = true;

    public Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void enable() {
        isActive = true;
    }

    public void disable() {
        isActive = false;
    }

    public void init(GameObject owner) {
        this.owner = owner;
    }

    public void update (float dt) {}

    public void finalize() {

    }

}
