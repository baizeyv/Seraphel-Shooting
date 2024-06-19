package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.seraphel.shooting.master.builtin.data.PipeData;

public class Pipe {

    public final PipeData data;

    public final Node node;

    public Launcher launcher;

    public Pipe(PipeData data, Node node) {
        if (data == null)
            throw new IllegalArgumentException("data is null");
        if (node == null)
            throw new IllegalArgumentException("node is null");
        this.data = data;
        this.node = node;
        setToSetupPose();
    }

    public void setComponent(Launcher launcher) {
        if (this.launcher == launcher)
            return;
        this.launcher = launcher;
    }

    public void setToSetupPose() {
        if (data.launcherName == null)
            setComponent(null);
        else {
            launcher = null;
            setComponent(node.nodeTree.getLauncher(data.index, data.launcherName));
        }
    }

    public void drawDebug(ShapeRenderer shapes) {
        float nodeX = node.getWorldX();
        float nodeY = node.getWorldY();
        shapes.setColor(Color.LIGHT_GRAY);
        shapes.triangle(nodeX, nodeY, nodeX - 10, nodeY - 10, nodeX + 10, nodeY - 10);
    }
}
