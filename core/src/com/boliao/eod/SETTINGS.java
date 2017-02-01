package com.boliao.eod;

/**
 * Created by mrboliao on 17/1/17.
 */

public final class SETTINGS {
    public static final int VIEWPORT_WIDTH = 720;
    public static final int VIEWPORT_HEIGHT = 1280;

    public static final int SPRITE_SIZE = 120;
    public static final int HOUSE_SIZE = 380;
    public static final int BLOCK_SIZE = 150;

    public static final int HOUSE_POS_X = 380;
    public static final int HOUSE_POS_Y = 1000;
    public static final int BLOCK_POS_X = 380;
    public static final int BLOCK_POS_Y = 300;
    public static final int PLAYER_POS_X = 400;
    public static final int PLAYER_POS_Y = 900;
    public static final int BUG_POS_X = 400;
    public static final int BUG_POS_Y = 100;


    public static final float SPEED_PLAYER = 500.0f;
    public static final float SPEED_BUG = 350.0f;
    public static final float FORCE_MULT = 3.0f;
    public static final float SLOW_RADIUS = 150.0f;
    public static final float STOP_RADIUS = 35.0f;
    public static final float STOP_RADIUS_BUG = 80.0f;
    public static final float DECEL_FACTOR = 100.0f;
    public static final float MIN_SPEED = 100.0f;

    public static final float MASS = 1.0f;
    public static final float PROXIMITY = STOP_RADIUS; // this is squared value
    public static final float MELEE_RANGE = 100.0f;

    public static final float COLLISION_FORWARD_LEN = 0.7f;
    public static final float COLLISION_FORCE = 100000.0f;
    public static final float COLLISION_VEC_OFFSET_DEG = 37.0f;

    public static final float ANIM_FRAME_TIME = 0.1f;
}
