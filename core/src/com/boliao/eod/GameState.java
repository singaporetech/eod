package com.boliao.eod;

/**
 * Created by mrboliao on 16/1/17.
 */

public class GameState {
    private static GameState instance = new GameState();

    private int steps;
    private int timer;
    private int numNights;
    private boolean canSpawn;

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

    public void setTimer(int timer) {
        this.timer = timer;
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
    }

    public int getNumNights() {
        return numNights;
    }

    public void setNumNights(int numNights) {
        this.numNights = numNights;
    }

    public void incNumNights() {
        ++numNights;
    }
}
