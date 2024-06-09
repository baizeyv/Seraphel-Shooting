package com.seraphel.shooting.master.builtin.data;

public class NodeData {

    public final int index;

    public final String name;

    public final NodeData parent;

    public float x, y, rotation, scaleX, scaleY, shearX, shearY;

    public NodeData(int index, String name, NodeData parent) {
        if (index < 0)
            throw new IllegalArgumentException("index must be greater than zero");
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.index = index;
        this.name = name;
        this.parent = parent;
    }

}
