package com.boliao.eod;

import com.boliao.eod.components.Health;

/**
 * Created by mrboliao on 16/1/17.
 * - a singleton class representing the entire game state
 */
public class GameState {
    private static final String TAG = "GameState";

    private static GameState instance = new GameState();

    private boolean isServiceStarted = false;
    private boolean isAppActive = true;

    private int steps;
    private int timer;
    private int numNights;
    private boolean canSpawn;
    private boolean canNotify;

    private Health playerHealth = null;

    private GameState() {
        reset();
    }

    public static GameState i() {
        return instance;
    }

    public void reset() {
        steps = 0;
        timer = SETTINGS.SECS_IN_DAY;
        numNights = 0;
        canSpawn = false;
    }

    public int getTimer() {
        return timer;
    }

    public void decTimer() {
        //Gdx.app.log(TAG, "decTimer()");
        --timer;
        if (timer == 0) {
            incNumNights();
            canSpawn = canNotify = true;
            timer = SETTINGS.SECS_IN_DAY;
        }
    }

    public boolean isCanNotify() {
        if (canNotify) {
            canNotify = false; // reset whenever return true
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isCanSpawn() {
        if (canSpawn) {
            canSpawn = false; // reset whenever return true
            return true;
        }
        else {
            return false;
        }
    }

    public int getSteps() {
        return steps;
    }

    public void incSteps(int inc) {
        steps += inc;

        // todo: should send a message to all subscribers and they can do their callback
        if (playerHealth != null) {
            playerHealth.heal(SETTINGS.HP_HEAL_AMT_STEPS);
        }
    }

    public int getNumNights() {
        return numNights;
    }

    public void incNumNights() {
        ++numNights;
    }

    public void setPlayerHealth(GameObject player) {
        playerHealth = (Health) player.getComponent("Health");
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }

    public void setServiceStarted(boolean serviceStarted) {
        isServiceStarted = serviceStarted;
    }

    public boolean isAppActive() {
        return isAppActive;
    }

    public void setAppActive(boolean appActive) {
        isAppActive = appActive;
    }
}
