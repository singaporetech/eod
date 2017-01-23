package com.boliao.eod;

import com.badlogic.gdx.graphics.Camera;
import com.boliao.eod.components.Engine;
import com.boliao.eod.components.Renderable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 19/1/17.
 */

public class RenderEngine implements Engine{

    private List<Renderable> renderables = new LinkedList<Renderable>();

    private static RenderEngine instance = new RenderEngine();
    public static RenderEngine i(){
        return instance;
    }

    private Camera cam;

    public void tick() {
        for (Renderable r: renderables) {
            r.draw();
        }
    }

    public void addRenderable(Renderable r) {
        renderables.add(r);
    }

    public void setCam (Camera cam) {
        this.cam = cam;
    }

    public Camera getCam () {
        return cam;
    }

}
