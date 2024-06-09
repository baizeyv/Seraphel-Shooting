package com.seraphel.shooting.master.builtin.data;

public class BarrageStateData {

    public final NodeTreeData nodeTreeData;

    public BarrageStateData(NodeTreeData nodeTreeData) {
        if (nodeTreeData == null)
            throw new IllegalArgumentException("nodeTreeData cannot be null");
        this.nodeTreeData = nodeTreeData;
    }
}
