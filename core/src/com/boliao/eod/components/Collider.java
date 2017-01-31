package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.GameObject;
import com.boliao.eod.RenderEngine;
import com.boliao.eod.SETTINGS;

/**
 * Created by mrboliao on 24/1/17.
 */

public class Collider extends Component implements Collidable, RenderableDebug {
    private static final String TAG = "Collider:C";

    Transform transform;
    Renderable renderable;

    //Polygon boundingPolygon;
    Circle boundingCircle;
    Vector2 collisionNorm = new Vector2(0, 0);
    Vector2 collisionVec = new Vector2();
    Vector2 collisionForwardPos = new Vector2();

    //protected Vector2 forwardVec = new Vector2(0, 0);
    float width = 0;
    float height = 0;

    public Collider() {
        super("Collider");
    }
    public Collider(float width, float height) {
        super("Collider");
        this.width = width;
        this.height = height;
    }

    @Override
    public void init(GameObject owner) {
        super.init(owner);

        // setup links
        transform = (Transform) owner.getComponent("Transform");

        // todo: do sprite sheet if no sprite, perhaps need a way to get only interface
        renderable = (Renderable) owner.getComponent("Sprite");
        if (renderable == null) {
            renderable = (Renderable) owner.getComponent("SpriteSheet");
        }

        // init bounding circle
        Rectangle rect = renderable.getBoundingBox();
        boundingCircle = new Circle(transform.getPos(), rect.getWidth()/2);

        // add to collision engine
        CollisionEngine.i().addCollidable(this);
        RenderEngine.i().addRenderableDebug(this);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // bounding circle tp match transform position and rotation
        boundingCircle.setPosition(transform.getPos());
        collisionVec.set(transform.getForward()).scl(SETTINGS.COLLISION_FORWARD_LEN);
        collisionForwardPos.set(transform.getPos()).add(collisionVec);
    }

    @Override
    /**
     * todo: draw the debug bounds
     */
    public void draw() {
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Line);

        // draw bounding circle
        RenderEngine.i().getDebugRenderer().setColor(0,1,0,1);
        RenderEngine.i().getDebugRenderer().circle(boundingCircle.x, boundingCircle.y, boundingCircle.radius);

        // draw the collision forward vector
        RenderEngine.i().getDebugRenderer().setColor(1,1,1,1);
        RenderEngine.i().getDebugRenderer().line(transform.getPos(), collisionForwardPos);

        // draw the collision force in the direction of collisionNorm
        RenderEngine.i().getDebugRenderer().setColor(0,0,1,1);
        Vector2 v1 = new Vector2(collisionNorm).scl(SETTINGS.COLLISION_FORCE).add(transform.getPos());
        RenderEngine.i().getDebugRenderer().line(transform.getPos(), v1);

        RenderEngine.i().getDebugRenderer().end();
    }

    @Override
    public Rectangle getBoundingBox() {
        return renderable.getBoundingBox();
    }

    /**
     * Get the collision position, i.e., centroid of collided polygon
     * @param other
     * @return null if no intersection
     */
    @Override
    public Vector2 getCollisionNorm(Collidable other) {
        float dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPos, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), collisionNorm);

        //Gdx.app.log(TAG, owner.getName() + ": collided=" + isCollided + ": mtv=" + mtv + " myPoly=" + boundingPolygon.getX() + "," + boundingPolygon.getY()+ " otherPoly=" + other.getBoundingPolygon().getX() + ","  + other.getBoundingPolygon().getY());

        if (dist != Float.POSITIVE_INFINITY) {
            return collisionNorm;
        }
        return null;
    }

    public Vector2 getBoundingCirclePos() {
        return new Vector2(boundingCircle.x, boundingCircle.y);
    }

    public float getBoundingCircleRadius() {
        return boundingCircle.radius;
    }
}
