package com.boliao.eod;

/**
 * Created by mrboliao on 16/1/17.
 */

public class GameState {
    private static GameState instance = new GameState();

    int steps = 0;
    int timer = 100;

    // game params
    public final int HUMAN_SPEED = 30;

    private GameState() {}

    public static GameState i() {
        return instance;
    }

    public void GameState() {
        --timer;
    }
}
