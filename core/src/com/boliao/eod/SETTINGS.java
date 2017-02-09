package com.boliao.eod;

/**
 * Created by mrboliao on 17/1/17.
 */

public final class SETTINGS {
    public static boolean IS_DEBUG = false;

    public static final int SECS_IN_DAY = 10;

    public static final int VIEWPORT_WIDTH = 720;
    public static final int VIEWPORT_HEIGHT = 1280;

    public static final int SPRITE_WIDTH = 512/4;
    public static final int SPRITE_HEIGHT = 384/4;
    public static final int HOUSE_SIZE = 380;
    public static final int BLOCK_SIZE = 150;
    public static final int X_SIZE = 50;
    public static final int PLUSONE_SIZE = 100;
    public static final int BAM_SIZE = 100;
    public static final int HEALTHBAR_WIDTH = 100;
    public static final int HEALTHBAR_HEIGHT = 8;
    public static final int HEALTHBAR_PLAYER_WIDTH = 620;
    public static final int HEALTHBAR_PLAYER_HEIGHT = 38;
    public static final int HEALTHBAR_LINEWIDTH = 5;


    public static final int HOUSE_POS_X = 380;
    public static final int HOUSE_POS_Y = 1000;
    public static final int BLOCK_POS_X = 380;
    public static final int BLOCK_POS_Y = 400;
    public static final int PLAYER_POS_X = 400;
    public static final int PLAYER_POS_Y = 900;
    public static final int BUG_POS_X = 400;
    public static final int BUG_POS_Y = 100;
    public static final int BUG_POS_JITTER_X = 80;
    public static final int HEALTHBAR_OFFSET_X = -SPRITE_WIDTH/2;
    public static final int HEALTHBAR_OFFSET_Y = SPRITE_HEIGHT/2 + 10;
    public static final int HEALTHBAR_PLAYER_X = 80;
    public static final int HEALTHBAR_PLAYER_Y = 1148;
    public static final int PLUSONE_OFFSET_X = 50;
    public static final int PLUSONE_OFFSET_Y = 20;

    public static final float SPEED_PLAYER = 500.0f;
    public static final float SPEED_BUG = 350.0f;
    public static final float FORCE_MULT = 3.0f;
    public static final float SLOW_RADIUS = 130.0f;
    public static final float STOP_RADIUS = 35.0f;
    public static final float STOP_RADIUS_PURSUE = 70.0f;
    public static final float DECEL_FACTOR = 100.0f;
    public static final float MIN_SPEED = 100.0f;

    public static final float MASS = 1.0f;
    public static final float MELEE_RANGE = 100.0f;

    public static final float COLLISION_FORWARD_LEN = 0.7f;
    public static final float COLLISION_FORCE = 100000.0f;
    public static final float COLLISION_VEC_OFFSET_DEG = 30.0f;
    public static final float COLLISION_TARGET_OFFSET = SPRITE_WIDTH + 30.0f;

    public static final float ANIM_FRAME_TIME = 0.12f;
    public static final float X_FADEOUT_DECREMENT = 1.5f;
    public static final float PLUSONE_FADEOUT_DECREMENT = 0.8f;
    public static final float ATTACK_DELAY_TIME = 0.6f;
    public static final float BAM_FADEOUT_DECREMENT = 2.5f;

    public static final float BUG_DMG = 1f;
    public static final float PLAYER_DMG = 10f;
    public static final float PLAYER_HP = 100f;
    public static final float HP_HEAL_AMT_STEPS = 1f;
}
