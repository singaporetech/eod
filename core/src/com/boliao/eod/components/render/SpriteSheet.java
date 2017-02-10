package com.boliao.eod.components.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.*;

/**
 * Created by mrboliao on 24/1/17.
 */

public class SpriteSheet extends Component implements Renderable {
    private static final String TAG = "SpriteSheet:C;R";

    public enum Sequence {RUN, MELEE, DESTRUCT}
    protected Sequence sequence;
    protected int startFrame, endFrame;

    protected Transform transform;
    protected TextureAtlas spriteSheet;
    protected Array<com.badlogic.gdx.graphics.g2d.Sprite> sprites;
    protected com.badlogic.gdx.graphics.g2d.Sprite currSprite;
    protected int currSpriteIndex = 0;
    protected boolean isAnimated = false;
    protected boolean isRepeat = true;
    protected float animationElapsedTime = 0;

    public SpriteSheet(String name, String spritePath, int width, int height) {
        super(name);

        // init spritesheet
        spriteSheet = new TextureAtlas(spritePath);
        sprites = spriteSheet.createSprites();
        currSprite = sprites.get(0);
        startFrame = endFrame = 0;

        // init sprites
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            //sprite.setOriginCenter();
            sprite.setSize(width, height);
            //sprite.setScale(0.1f);
            sprite.setOriginCenter();
        }
    }

    public SpriteSheet(String name, String spritePath, int size) {
        this(name, spritePath, size, size);
    }

    public SpriteSheet (String spritePath, int size) {
        this("SpriteSheet", spritePath, size);
    }

    public SpriteSheet(String spritePath) {
        this("SpriteSheet", spritePath, SETTINGS.SPRITE_WIDTH, SETTINGS.SPRITE_HEIGHT);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");

        // add to render engine
        RenderEngine.i().addRenderable(this);
    }

    @Override
    public Rectangle getBoundingBox() {
        return currSprite.getBoundingRectangle();
    }

    public void setSequence(Sequence seq) {
        this.sequence = seq;
        switch(seq) {
            case RUN:
                startFrame = 0;
                endFrame = 3;
                break;

            case MELEE:
                startFrame = 4;
                endFrame = 8;
                break;
        }
        currSpriteIndex = startFrame;
    }

    public void setAlpha(float alpha) {
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            sprite.setAlpha(alpha);
        }
    }

    @Override
    public void update(float delta) {
        // set position
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            sprite.setRotation(transform.getRot());
            sprite.setCenter(transform.getX(), transform.getY());
        }

        // do animation
        if (isAnimated) {
            animationElapsedTime += delta;
            if (animationElapsedTime > SETTINGS.ANIM_FRAME_TIME) {
                ++currSpriteIndex;
                if (currSpriteIndex > endFrame) {
                    if (isRepeat) {
                        currSpriteIndex = startFrame;
                    }
                    else {
                        --currSpriteIndex;
                        isAnimated = false;
                    }
                }
                animationElapsedTime = 0;
                currSprite = sprites.get(currSpriteIndex);
            }
        }
    }

    public void onAnimation(Sequence seq, boolean isRepeat) {
        isAnimated = true;
        this.isRepeat = isRepeat;
        setSequence(seq);
    }

    public void onAnimation(Sequence seq) {
        onAnimation(seq, true);
    }

    public void offAnimation() {
        isAnimated = false;
        isRepeat = true;
    }

    public void draw() {
        currSprite.draw(RenderEngine.i().getSpriteBatch());
    }

    @Override
    public void finalize() {
        super.finalize();

        // opengl textures are not auto deleted
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            sprite.getTexture().dispose();
        }
        
        // remove from render engine
        RenderEngine.i().removeRenderable(this);
    }
}
