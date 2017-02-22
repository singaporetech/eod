package com.boliao.eod.components.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;
import com.boliao.eod.components.Component;
import com.boliao.eod.components.Transform;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Sprite extends Component implements Renderable {
    private static final String TAG = "Sprite:C;R";
    protected Transform transform;
    protected com.badlogic.gdx.graphics.g2d.Sprite sprite;

    protected float alpha = 1;
    protected float scale = 1;

    public Sprite(String name, String spritePath, int width, int height) {
        super(name);

        // init sprite
        sprite = new com.badlogic.gdx.graphics.g2d.Sprite(new Texture(spritePath));
        sprite.setSize(width, height);
        //sprite.setScale(0.1f);
        sprite.setOriginCenter();
    }

    public Sprite(String spritePath, int sizeX, int sizeY) {
        this("Sprite", spritePath, sizeX, sizeY);
    }

    public Sprite(String spritePath, int size) {
        this(spritePath, size, size);
    }

    public Sprite(String spritePath) {
        this(spritePath, SETTINGS.SPRITE_WIDTH, SETTINGS.SPRITE_HEIGHT);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");

        // add to Render Engine
        RenderEngine.i().addRenderable(this);
    }

    @Override
    public Rectangle getBoundingBox() {
        return sprite.getBoundingRectangle();
    }

    public void setPos(float x, float y) {
        sprite.setCenter(x, y);
    }
    public void setPos(Vector2 pos) {
        setPos(pos.x, pos.y);
    }


    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        sprite.setAlpha(alpha);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        sprite.setScale(scale);
    }

    public void shrink(float dec, float dt) {
        scale -= dec * dt;
        if (scale < 0) {
            scale = 0;
        }
        sprite.setScale(scale);
    }

    public void reset() {
        setAlpha(1);
        setScale(1);
    }

    public void fadeOut(float dec, float dt) {
        alpha -= dec * dt;
        if (alpha < 0) {
            alpha = 0;
        }
        sprite.setAlpha(alpha);
    }

    public void shrinkAndFade(float dec, float dt) {
        shrink(dec, dt);
        fadeOut(dec, dt);
    }

    @Override
    public void update(float dt) {
        if (isActive) {
            // follow transforms position
            //sprite.setPosition(transform.getX(), transform.getY());
            sprite.setRotation(transform.getRot());
            sprite.setCenter(transform.getX(), transform.getY());
        }
    }

    public void draw() {
        if (isActive) {
            sprite.draw(RenderEngine.i().getSpriteBatch());
        }
    }

    @Override
    public void finalize() {
        super.finalize();

        // opengl textures are not auto deleted
        sprite.getTexture().dispose();

        // remove from render engine
        RenderEngine.i().removeRenderable(this);
    }
}
