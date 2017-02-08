package com.boliao.eod;

import com.boliao.eod.components.Health;

/**
 * Created by mrboliao on 16/1/17.
 */

public class GameState {
    private static GameState instance = new GameState();

    private int steps;
    private int timer;
    private int numNights;
    private boolean canSpawn;

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
        --timer;
        if (timer == 0) {
            incNumNights();
            canSpawn = true;
            timer = SETTINGS.SECS_IN_DAY;
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
}
