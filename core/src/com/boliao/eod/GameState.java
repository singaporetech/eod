package com.boliao.eod;

/**
 * Created by mrboliao on 16/1/17.
 */

public class GameState {
    private static GameState instance = new GameState();

    private int steps = 0;
    private int timer = SETTINGS.SECS_IN_DAY;
    private int numNights = 0;

    private GameState() {}

    public static GameState i() {
        return instance;
    }

    public void GameState() {
        --timer;
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
            timer = SETTINGS.SECS_IN_DAY;
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
