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
    private Transform transform;
    private com.badlogic.gdx.graphics.g2d.Sprite sprite;

    private float spriteAlpha = 1;

    public Sprite(String name, String spritePath, int sizeX, int sizeY) {
        super(name);

        // init spritesheet
        sprite = new com.badlogic.gdx.graphics.g2d.Sprite(new Texture(spritePath));

        //sprite.setOriginCenter();
        sprite.setSize(sizeX, sizeY);
        //sprite.setScale(0.1f);
        sprite.setOriginCenter();

        // add to Render Engine
        RenderEngine.i().addRenderable(this);
    }

    public Sprite(String spritePath, int sizeX, int sizeY) {
        this("Sprite", spritePath, sizeX, sizeY);
    }

    public Sprite(String spritePath, int size) {
        this(spritePath, size, size);
    }

    public Sprite(String spritePath) {
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
        return sprite.getBoundingRectangle();
    }

    public void setPos(Vector2 pos) {
        sprite.setCenter(pos.x, pos.y);
    }

    public float getSpriteAlpha() {
        return spriteAlpha;
    }

    public void setSpriteAlpha(float spriteAlpha) {
        this.spriteAlpha = spriteAlpha;
    }

    public void fadeOut(float dec, float delta) {
        spriteAlpha -= dec * delta;
        if (spriteAlpha < 0) {
            spriteAlpha = 0;
        }
        sprite.setAlpha(spriteAlpha);
    }

    @Override
    public void update(float delta) {
        // follow transforms position
        //sprite.setPosition(transform.getX(), transform.getY());
        sprite.setRotation(transform.getRot());
        sprite.setCenter(transform.getX(), transform.getY());
    }

    public void draw() {
        sprite.draw(RenderEngine.i().getSpriteBatch());
    }

    @Override
    public void finalize() {
        super.finalize();

        // opengl textures are not auto deleted
        sprite.getTexture().dispose();
    }
}
