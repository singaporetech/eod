package com.boliao.eod.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.boliao.eod.CollisionEngine;
import com.boliao.eod.Game;
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

        // init polygon

//        if (width == 0 && height == 0) {
//            boundingPolygon = new Polygon(new float[]{
//                    -rect.width / 2, rect.height / 2,
//                    rect.width / 2, rect.height / 2,
//                    rect.width / 2, -rect.height / 2,
//                    -rect.width / 2, -rect.height / 2
//            });
//        }
//        else {
//            boundingPolygon = new Polygon(new float[]{
//                    -width / 2, height / 2,
//                    width / 2, height / 2,
//                    width / 2, -height / 2,
//                    -width / 2, -height / 2
//            });
//        }
//        boundingPolygon = new Polygon(new float[]{
//                -rect.width/2,-rect.height/2,
//                rect.width/2,-rect.height/2,
//                rect.width/2,rect.height/2,
//                -rect.width/2,rect.height/2,
//                -rect.width/2,-rect.height/2
//        });
        //boundingPolygon.setOrigin(rect.width/2, rect.height/2);

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

//        Vector2 pos = new Vector2(transform.getPos());
//        pos.add(forwardVec);
//        boundingPolygon.setRotation(transform.getRot());
//        boundingPolygon.setPosition(pos.x, pos.y);

//        if (owner.getName() == "player") {
//            Gdx.app.log(TAG, "player::update: pos=" + pos + " forwardVec="+ forwardVec);
//        }

        // todo: put in testcase
//        boolean aaa = hasCollidedWithLine(new Vector2(380,0), new Vector2(380, 1000));
//        boolean bbb = hasCollidedWithLine(new Vector2(379,0), new Vector2(379, 1000));
//        boolean aac = hasCollidedWithLine(new Vector2(420,0), new Vector2(420, 1000));
//        boolean bba = hasCollidedWithLine(new Vector2(0,301), new Vector2(390, 301));
//        boolean abb = hasCollidedWithLine(new Vector2(0,0), new Vector2(500, 500));
//        boolean bca = hasCollidedWithLine(new Vector2(0,299), new Vector2(20, 299));
    }

    @Override
    /**
     * todo: draw the debug bounds
     */
    public void draw() {
        RenderEngine.i().getDebugRenderer().begin(ShapeRenderer.ShapeType.Line);
        RenderEngine.i().getDebugRenderer().setColor(0,1,0,1);
//        RenderEngine.i().getDebugRenderer().polygon(boundingPolygon.getTransformedVertices());
        RenderEngine.i().getDebugRenderer().circle(boundingCircle.x, boundingCircle.y, boundingCircle.radius);

        // draw the collision vector
        RenderEngine.i().getDebugRenderer().setColor(1,1,1,1);
        RenderEngine.i().getDebugRenderer().line(transform.getPos(), collisionForwardPos);

        // draw the collision force in the direction of collisionNorm
        RenderEngine.i().getDebugRenderer().setColor(0,1,1,1);
        Vector2 v1 = new Vector2(collisionNorm).scl(SETTINGS.COLLISION_FORCE).add(transform.getPos());
        RenderEngine.i().getDebugRenderer().line(transform.getPos(), v1);

        RenderEngine.i().getDebugRenderer().end();
    }

//    public void setForwardVec(Vector2 vec) {
//        forwardVec.set(vec);
//    }

    @Override
    public Rectangle getBoundingBox() {
        return renderable.getBoundingBox();
    }

//    @Override
//    public Polygon getBoundingPolygon() {
//        return boundingPolygon;
//    }

//    @Override
//    public boolean hasCollidedWithLine(Vector2 pos1, Vector2 pos2) {
//        return Intersector.intersectSegmentPolygon(pos1, pos2, boundingPolygon);
//    }

    /**
     * Get the collision position, i.e., centroid of collided polygon
     * @param other
     * @return null if no intersection
     */
    @Override
    public Vector2 getCollisionNorm(Collidable other) {
//        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
//        Boolean isCollided = Intersector.overlapConvexPolygons(other.getBoundingPolygon(), boundingPolygon, mtv);
        float dist = Intersector.intersectSegmentCircleDisplace(transform.getPos(), collisionForwardPos, other.getBoundingCirclePos(), other.getBoundingCircleRadius(), collisionNorm);

        //Gdx.app.log(TAG, owner.getName() + ": collided=" + isCollided + ": mtv=" + mtv + " myPoly=" + boundingPolygon.getX() + "," + boundingPolygon.getY()+ " otherPoly=" + other.getBoundingPolygon().getX() + ","  + other.getBoundingPolygon().getY());

        if (dist != Float.POSITIVE_INFINITY) {
//        if (Intersector.intersectPolygons(other.getBoundingPolygon(), boundingPolygon, retPoly)) {
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

//    @Override
//    public Vector2 getCollisionDir(Vector2 forwardPos) {
//        return new Vector2(forwardPos).sub(transform.getPos()).nor();
//    }
}
