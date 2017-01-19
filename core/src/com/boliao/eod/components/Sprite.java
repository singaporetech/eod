package com.boliao.eod.components;

import com.badlogic.gdx.graphics.Texture;
import com.boliao.eod.Game;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;

/**
 * Created by mrboliao on 19/1/17.
 */

public class Sprite extends Component implements Renderable {
    protected com.badlogic.gdx.graphics.g2d.Sprite sprite;
    private Transform transform;

    public Sprite(String spritePath) {
        super("Sprite");

        sprite = new com.badlogic.gdx.graphics.g2d.Sprite(new Texture(spritePath));

        // add to Render Engine
        RenderEngine.i().addRenderable(this);
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");
    }

    @Override
    public void update(float delta) {
        // follow transforms position
        sprite.setPosition(transform.getX(), transform.getY());
    }

    public void draw() {
        sprite.draw(Game.i().spriteBatch);
    }

}
