package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.seraphel.shooting.master.builtin.Event;
import com.seraphel.shooting.master.builtin.Projectile;
import com.seraphel.shooting.master.builtin.ProjectileState;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.data.ProjectileData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;

public class TestBulletActor extends Actor implements Projectile {

    ProjectileData data;

    ProjectileState state;

    public TestBulletActor(ProjectileData data) {
        // TODO:
        this.data = data;
        this.state = new ProjectileState(data);
        // TODO: add Timelines;
        EventTimeline eventTimeline = new EventTimeline(10);
        for (int i = 0; i < 10; i++) {
            float time = (i + 1) * 1;
            EventData eventData = new EventData("TestEventName");
            Event event = new Event(time, eventData);
            eventTimeline.setFrame(i, event);
        }
        timelines.add(eventTimeline);
    }

    @Override
    public void act(float delta) {
        state.update(delta);
        state.apply();
        super.act(delta);
    }
}
