package com.boliao.eod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.boliao.eod.components.render.Renderable;
import com.boliao.eod.components.render.RenderableDebug;

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
    public void init() {
        // create sprite drawer
        spriteBatch = new SpriteBatch();

        // create camera
        cam = new OrthographicCamera();

        // create viewport
        viewport = new FitViewport(SETTINGS.VIEWPORT_WIDTH, SETTINGS.VIEWPORT_HEIGHT, cam);
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        // create heads up display
        hud = new Hud();

        // debug renderer
        initDebugRenderer();
    }

    public void tick() {
        // perform updates
        cam.update();
        hud.update();

        // clear screen
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw all game objects
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        for (Renderable r: renderables) {
            r.draw();
        }
        spriteBatch.end();

        // draw debug graphics
        shapeRenderer.setProjectionMatrix(cam.combined);
        for (RenderableDebug r: renderableDebugs) {
            r.draw();
        }

        // draw hud
        spriteBatch.setProjectionMatrix(hud.getStageCam().combined);
        hud.draw();
    }

    public void addRenderable(Renderable r) {
        renderables.add(r);
    }
    public void removeRenderable(Renderable r) {
        renderables.remove(r);
    }

    public void addRenderableDebug(RenderableDebug r) {
        renderableDebugs.add(r);
    }
    public void removeRenderableDebug(RenderableDebug r) {
        renderableDebugs.remove(r);
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

    public void initDebugRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void shutdownDebugRenderer() {
        shapeRenderer.dispose();
    }

    public void setViewport(int width, int height) {
        viewport.update(width, height);
    }

    public void showEndGameMenu() {
        hud.showEndGameMenu();
    }

    public void hideEndGameMenu() {
        hud.hideEndGameMenu();
    }

    public void clearRenderables() {
        renderables.clear();
        renderableDebugs.clear();
    }

    @Override
    public void finalize() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
