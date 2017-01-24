package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.boliao.eod.components.Renderable;
import com.boliao.eod.components.RenderableDebug;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 19/1/17.
 */

public class RenderEngine implements Engine{
    private List<Renderable> renderables = new LinkedList<Renderable>();
    private List<RenderableDebug> renderableDebugs = new LinkedList<RenderableDebug>();

    private static RenderEngine instance = new RenderEngine();
    public static RenderEngine i(){
        return instance;
    }
    private RenderEngine() {}

    private SpriteBatch spriteBatch;
    private Camera cam;
    private Hud hud;
    private Viewport viewport;

    // debug renderer
    protected ShapeRenderer shapeRenderer;

    @Override
    public void init() {// camera
        spriteBatch = new SpriteBatch();
        cam = new OrthographicCamera();
        hud = new Hud();
        RenderEngine.i().setCam(cam);
        viewport = new FitViewport(SETTINGS.VIEWPORT_WIDTH, SETTINGS.VIEWPORT_HEIGHT, cam);
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        // debug renderer
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
    }

    public void tick() {
        // perform updates
        cam.update();
        hud.update();

        // clear screen
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw hud
        spriteBatch.setProjectionMatrix(hud.getStageCam().combined);
        hud.draw();

        // draw all game objects
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        for (Renderable r: renderables) {
            r.draw();
        }
        spriteBatch.end();

        // draw debug graphics
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (RenderableDebug r: renderableDebugs) {
            r.draw();
        }
        shapeRenderer.end();
    }

    public void addRenderable(Renderable r) {
        renderables.add(r);
    }

    public void addRenderableDebug(RenderableDebug r) {
        renderableDebugs.add(r);
    }

    public void setCam (Camera cam) {
        this.cam = cam;
    }

    public Camera getCam () {
        return cam;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public ShapeRenderer getDebugRenderer() {
        return shapeRenderer;
    }

    public void setViewport(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void finalize() {
        spriteBatch.dispose();
    }
}
