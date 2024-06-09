package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.data.NodeData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;
import com.seraphel.shooting.master.builtin.data.PipeData;

public class NodeTree {

    public final NodeTreeData data;

    public final Array<Node> nodes;

    public final Array<Pipe> pipes;

    public Array<Pipe> drawOrder;

    public LauncherCollector componentCollector;

    public float scaleX = 1, scaleY = 1;

    public float x, y;

    public NodeTree(NodeTreeData data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
        this.data = data;

        nodes = new Array<Node>(data.nodes.size);
        for (NodeData nodeData : data.nodes) {
            Node node;
            if (nodeData.parent == null)
                node = new Node(nodeData, this, null);
            else {
                Node parent = nodes.get(nodeData.parent.index);
                node = new Node(nodeData, this, parent);
                parent.children.add(node);
            }
            nodes.add(node);
        }

        pipes = new Array<Pipe>(data.pipes.size);
        drawOrder = new Array<Pipe>(data.pipes.size);
        for (PipeData pipeData : data.pipes) {
            Node node = nodes.get(pipeData.nodeData.index);
            Pipe pipe = new Pipe(pipeData, node);
            pipes.add(pipe);
            drawOrder.add(pipe);
        }
    }

    public void updateWorldTransform() {
        Array<Node> nodes = this.nodes;
        for (int i = 0, n = nodes.size; i < n; i++) {
            nodes.get(i).update();
        }
    }

    public Launcher getComponent(String pipeName, String componentName) {
        PipeData pipe = data.findPipe(pipeName);
        if (pipe == null)
            throw new IllegalArgumentException("Pipe " + pipeName + " not found");
        return getComponent(pipe.index, componentName);
    }

    public Launcher getComponent(int pipeIndex, String componentName) {
        if (componentName == null)
            throw new IllegalArgumentException("componentName cannot be null");
        if (componentCollector != null) {
            Launcher launcher = componentCollector.getLauncher(pipeIndex, componentName);
            if (launcher != null)
                return launcher;
        }
        if (data.defaultCollector != null)
            return data.defaultCollector.getLauncher(pipeIndex, componentName);
        return null;
    }

}
