package com.boliao.eod;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrboliao on 19/1/17.
 */

public class RenderEngine {

    private List<com.boliao.eod.components.Renderable> renderables = new LinkedList<com.boliao.eod.components.Renderable>();

    private static RenderEngine instance = new RenderEngine();
    public static RenderEngine i(){
        return instance;
    }

    public void tick() {
        for (com.boliao.eod.components.Renderable r: renderables) {
            r.draw();
        }
    }

    public void addRenderable(com.boliao.eod.components.Renderable r) {
        renderables.add(r);
    }
}
