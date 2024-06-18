package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.seraphel.shooting.constant.Asset;
import com.seraphel.shooting.master.builtin.BarrageState;
import com.seraphel.shooting.master.builtin.NodeTree;
import com.seraphel.shooting.master.builtin.data.BarrageStateData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;

public class TestActor extends Actor {

    private NodeTree nodeTree;

    BarrageState state;

    public TestActor() {
        // TODO:
        NodeTreeData data = Asset.fetch().loadNodeTree("testShoot.json");
        nodeTree = new NodeTree(data);
        BarrageStateData barrageStateData = new BarrageStateData(data);
        state = new BarrageState(barrageStateData);

        state.play("main", true);

        debug();
    }

    @Override
    public void act(float delta) {
        state.update(delta);
        state.apply();
        super.act(delta);
        nodeTree.updateWorldTransform();
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
        nodeTree.drawDebug(shapes);
    }
}
