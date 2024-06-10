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
/*
        NodeTreeData nodeTreeData = new NodeTreeData();
        nodeTreeData.name = "TestName";
        NodeData nodeData = new NodeData(0, "root", null);
        nodeTreeData.nodes.add(nodeData);

        ShootTimeline shootTimeline = new ShootTimeline(10);
        EventTimeline eventTimeline = new EventTimeline(10);
        for (int i = 0; i < 10; i++) {
            float time = (i + 1) * 1;
            shootTimeline.setFrame(i, time);
            EventData eventData = new EventData("TestEventName");
            Event event = new Event(time, eventData);
            eventTimeline.setFrame(i, event);
        }
        Array<Timeline> arr = new Array<Timeline>();
        arr.add(shootTimeline);
        arr.add(eventTimeline);
        ArrayMap<String, Array<Timeline>> tc = new ArrayMap<String, Array<Timeline>>();
        tc.put("default", arr);
        Barrage barrage = new Barrage("main", tc, 10);

        nodeTreeData.barrages.add(barrage);

        nodeTree = new NodeTree(nodeTreeData);

        BarrageStateData barrageStateData = new BarrageStateData(nodeTreeData);
        state = new BarrageState(barrageStateData);

        state.play("main", false);
*/
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
