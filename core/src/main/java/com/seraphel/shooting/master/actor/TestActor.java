package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.Barrage;
import com.seraphel.shooting.master.builtin.BarrageState;
import com.seraphel.shooting.master.builtin.Event;
import com.seraphel.shooting.master.builtin.NodeTree;
import com.seraphel.shooting.master.builtin.data.BarrageStateData;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.data.NodeData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;
import com.seraphel.shooting.master.builtin.timeline.ShootTimeline;
import com.seraphel.shooting.master.builtin.timeline.Timeline;

public class TestActor extends Actor {

    private NodeTree nodeTree;

    BarrageState state;

    public TestActor() {
        // TODO:
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
        Barrage barrage = new Barrage("main", arr, 10);

        nodeTreeData.barrages.add(barrage);

        nodeTree = new NodeTree(nodeTreeData);

        BarrageStateData barrageStateData = new BarrageStateData(nodeTreeData);
        state = new BarrageState(barrageStateData);

        state.play("main", false);
    }

    @Override
    public void act(float delta) {
        state.update(delta);
        state.apply();
        super.act(delta);
        nodeTree.updateWorldTransform();
    }
}
