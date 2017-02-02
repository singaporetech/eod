package com.boliao.eod.components.ai;

/**
 * Created by mrboliao on 24/1/17.
 */

// todo: implement State class that contains the relevant components to start/stop them
public interface FsmState {
    void enter();
    void act();
    void exit();
}
