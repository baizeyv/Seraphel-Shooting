package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.data.NodeData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;
import com.seraphel.shooting.master.builtin.data.PipeData;
import com.seraphel.shooting.master.extend.Emitter;

public class NodeTree {

    public final NodeTreeData data;

    public final Array<Node> nodes;

    public final Array<Pipe> pipes;

    public Array<Pipe> drawOrder;

    public LauncherCollector launcherCollector;

    public float scaleX = 1, scaleY = 1;

    public float x, y;

    public NodeTree(NodeTreeData data, VirtualMethod virtualMethod) {
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

        pipes = new Array<>(data.pipes.size);
        drawOrder = new Array<>(data.pipes.size);
        for (PipeData pipeData : data.pipes) {
            Node node = nodes.get(pipeData.nodeData.index);
            Pipe pipe = new Pipe(pipeData, node);
            pipes.add(pipe);
            drawOrder.add(pipe);
        }

        Array<Launcher> launchers = new Array<>();
        if (launcherCollector != null) {
            launchers = launcherCollector.getAllLauncher();
        } else if (data.defaultCollector != null) {
            launchers = data.defaultCollector.getAllLauncher();
        }
        for (Launcher launcher : launchers) {
            launcher.setupEntity(this, virtualMethod);
        }
    }

    public void updateWorldTransform() {
        Array<Node> nodes = this.nodes;
        for (int i = 0, n = nodes.size; i < n; i++) {
            nodes.get(i).update();
        }
    }

    public Launcher getLauncher(String pipeName, String launcherName) {
        PipeData pipe = data.findPipe(pipeName);
        if (pipe == null)
            throw new IllegalArgumentException("Pipe " + pipeName + " not found");
        return getLauncher(pipe.index, launcherName);
    }

    public Launcher getLauncher(int pipeIndex, String launcherName) {
        if (launcherName == null)
            throw new IllegalArgumentException("componentName cannot be null");
        if (launcherCollector != null) {
            Launcher launcher = launcherCollector.getLauncher(pipeIndex, launcherName);
            if (launcher != null)
                return launcher;
        }
        if (data.defaultCollector != null)
            return data.defaultCollector.getLauncher(pipeIndex, launcherName);
        return null;
    }

    public Node findNode(String nodeName) {
        if (nodeName == null)
            throw new IllegalArgumentException("nodeName cannot be null");
        Array<Node> nodes = this.nodes;
        for (int i = 0, n = nodes.size; i < n; i++) {
            Node node = nodes.get(i);
            if (node.data.name.equals(nodeName))
                return node;
        }
        return null;
    }

    public void drawDebug(ShapeRenderer shapes) {
        Array<Launcher> launchers = new Array<>();
        if (launcherCollector != null) {
            launchers = launcherCollector.getAllLauncher();
        } else if (data.defaultCollector != null) {
            launchers = data.defaultCollector.getAllLauncher();
        }
        for (Launcher launcher : launchers) {
            if (launcher instanceof Emitter) {
                ((Emitter) launcher).drawDebug(shapes);
            }
        }
        for (Node node : this.nodes) {
            if (node != null)
                node.drawDebug(shapes);
        }
    }

}
