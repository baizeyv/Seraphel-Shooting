package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.data.NodeData;

public class Node {

    public final NodeData data;

    public final NodeTree nodeTree;

    public final Node parent;

    public final Array<Node> children = new Array<Node>();

    public float x, y, rotation, scaleX, scaleY, shearX, shearY;

    float a, b, worldX;

    float c, d, worldY;

    public Node(NodeData data, NodeTree nodeTree, Node parent) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
        if (nodeTree == null)
            throw new IllegalArgumentException("nodeTree cannot be null");
        this.data = data;
        this.nodeTree = nodeTree;
        this.parent = parent;
        setToSetupPose();
    }

    public void setToSetupPose() {
        NodeData data = this.data;
        x = data.x;
        y = data.y;
        rotation = data.rotation;
        scaleX = data.scaleX;
        scaleY = data.scaleY;
        shearX = data.shearX;
        shearY = data.shearY;
    }

    public void update() {
        updateWorldTransform(x, y, rotation, scaleX, scaleY, shearX, shearY);
    }

    public void updateWorldTransform(float x, float y, float rotation, float scaleX, float scaleY, float shearX, float shearY) {
        Node parent = this.parent;
        if (parent == null) { // root node
            NodeTree nodeTree = this.nodeTree;
            float rotationY = rotation + 90 + shearY, sx = nodeTree.scaleX, sy = nodeTree.scaleY;
            // a -> x缩放
            // b -> x斜切
            // c -> y斜切
            // d -> y缩放
            a = MathUtils.cosDeg(rotation + shearX) * scaleX * sx;
            b = MathUtils.cosDeg(rotationY) * scaleY * sx;
            c = MathUtils.sinDeg(rotation + shearX) * scaleX * sy;
            d = MathUtils.sinDeg(rotationY) * scaleY * sy;
            worldX = x * sx + nodeTree.x;
            worldY = y * sy + nodeTree.y;
            return;
        }

        float pa = parent.a, pb = parent.b, pc = parent.c, pd = parent.d;
        worldX = pa * x + pb * y + parent.worldX;
        worldY = pc * x + pd * y + parent.worldY;

        float rotationY = rotation + 90 + shearY;
        float la = MathUtils.cosDeg(rotation + shearX) * scaleX;
        float lb = MathUtils.cosDeg(rotationY) * scaleY;
        float lc = MathUtils.sinDeg(rotation + shearX) * scaleX;
        float ld = MathUtils.sinDeg(rotationY) * scaleY;
        a = pa * la + pb * lc;
        b = pa * lb + pb * ld;
        c = pc * la + pd * lc;
        d = pc * lb + pd * ld;
    }

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public float getWorldRotationX() {
        return MathUtils.atan2(c, a) * MathUtils.radiansToDegrees;
    }

    public float getWorldRotationY() {
        return MathUtils.atan2(d, b) * MathUtils.radiansToDegrees;
    }

    public float getWorldScaleX() {
        return (float) Math.sqrt(a * a + c * c);
    }

    public float getWorldScaleY() {
        return (float) Math.sqrt(b * b + d * d);
    }

    public Vector2 worldToLocal(Vector2 world) {
        if (world == null) throw new IllegalArgumentException("world cannot be null.");
        float invDet = 1 / (a * d - b * c);
        float x = world.x - worldX, y = world.y - worldY;
        world.x = x * d * invDet - y * b * invDet;
        world.y = y * a * invDet - x * c * invDet;
        return world;
    }

    /**
     * Transforms a point from the bone's local coordinates to world coordinates.
     */
    public Vector2 localToWorld(Vector2 local) {
        if (local == null) throw new IllegalArgumentException("local cannot be null.");
        float x = local.x, y = local.y;
        local.x = x * a + y * b + worldX;
        local.y = x * c + y * d + worldY;
        return local;
    }

    /**
     * Transforms a world rotation to a local rotation.
     */
    public float worldToLocalRotation(float worldRotation) {
        float sin = MathUtils.sinDeg(worldRotation), cos = MathUtils.cosDeg(worldRotation);
        return MathUtils.atan2(a * sin - c * cos, d * cos - b * sin) * MathUtils.radiansToDegrees + rotation - shearX;
    }

    /**
     * Transforms a local rotation to a world rotation.
     */
    public float localToWorldRotation(float localRotation) {
        localRotation -= rotation - shearX;
        float sin = MathUtils.sinDeg(localRotation), cos = MathUtils.cosDeg(localRotation);
        return MathUtils.atan2(cos * c + sin * d, cos * a + sin * b) * MathUtils.radiansToDegrees;
    }

    public void drawDebug(ShapeRenderer shapes) {
        float xx = getWorldX();
        float yy = getWorldY();
        float rt = getWorldRotationX();
        float sclX = getWorldScaleX();
        float sclY = getWorldScaleY();
        float unit = 20;
        shapes.setColor(Color.YELLOW);
        shapes.rect(xx - unit / 2f, yy - unit / 2f, unit / 2f, unit / 2f, unit, unit, sclX, sclY, rt);
    }
}
