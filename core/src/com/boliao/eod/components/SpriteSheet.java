package com.boliao.eod.components;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.boliao.eod.Game;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 24/1/17.
 */

public class SpriteSheet extends Component implements Renderable, FsmState {
    protected Transform transform;
    protected TextureAtlas spriteSheet;
    protected Array<com.badlogic.gdx.graphics.g2d.Sprite> sprites;
    protected com.badlogic.gdx.graphics.g2d.Sprite currSprite;
    protected int currSpriteIndex = 0;
    protected boolean isAnimated = false;
    protected float animationElapsedTime = 0;

    /**
     * Use the same name
     * @param spritePath
     * @param size
     */
    public SpriteSheet(String spritePath, int size) {
        super("SpriteSheet");

        // init spritesheet
        spriteSheet = new TextureAtlas(spritePath);
        sprites = spriteSheet.createSprites();
        currSprite = sprites.get(0);

        // init sprites
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            //sprite.setOriginCenter();
            sprite.setSize(size, size);
            //sprite.setScale(0.1f);
            sprite.setOriginCenter();
        }

        // add to render engine
        RenderEngine.i().addRenderable(this);
    }

    public SpriteSheet(String spritePath) {
        this(spritePath, SETTINGS.SPRITE_SIZE);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");
    }

    @Override
    public Rectangle getBoundingBox() {
        return currSprite.getBoundingRectangle();
    }

    @Override
    public void update(float delta) {
        // follow transforms position
        for (com.badlogic.gdx.graphics.g2d.Sprite sprite: sprites) {
            sprite.setRotation(transform.getRot());
            sprite.setCenter(transform.getX(), transform.getY());
        }

        // do animation
        if (isAnimated) {
            animationElapsedTime += delta;
            if (animationElapsedTime > SETTINGS.ANIM_FRAME_TIME) {
                currSpriteIndex = (currSpriteIndex == sprites.size-1) ? 0 : ++currSpriteIndex;
                currSprite = sprites.get(currSpriteIndex);
                animationElapsedTime = 0;
            }
        }
    }

    @Override
    public void enter() {
        isAnimated = true;
    }

    @Override
    public void exit() {
        isAnimated = false;
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
    }
}
