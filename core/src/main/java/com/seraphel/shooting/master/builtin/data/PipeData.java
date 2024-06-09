package com.seraphel.shooting.master.builtin.data;

public class PipeData {

    public final int index;

    public final String name;

    public final NodeData nodeData;

    public String launcherName;

    public PipeData(int index, String name, NodeData nodeData) {
        if (index < 0)
            throw new IllegalArgumentException("index < 0");
        if (name == null)
            throw new IllegalArgumentException("name == null");
        if (nodeData == null)
            throw new IllegalArgumentException("nodeData == null");
        this.index = index;
        this.name = name;
        this.nodeData = nodeData;
    }
}
