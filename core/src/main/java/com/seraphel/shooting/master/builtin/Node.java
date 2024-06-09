package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.math.MathUtils;
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

}
