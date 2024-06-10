package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.math.MathUtils;
import com.seraphel.shooting.actor.SpriteActor;
import com.seraphel.shooting.master.builtin.Event;
import com.seraphel.shooting.master.builtin.Node;
import com.seraphel.shooting.master.builtin.Projectile;
import com.seraphel.shooting.master.builtin.ProjectileState;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.BulletData;

public class TestBulletActor extends SpriteActor implements Projectile {

    BulletData data;

    ProjectileState state;

    private Emitter emitter;

    private Node node;

    public TestBulletActor(String atlasPath, String regionName, BulletData data, Emitter emitter) {
        super(atlasPath, regionName);
        // TODO:
        this.emitter = emitter;
        this.data = data;
        this.state = new ProjectileState(data);
        // TODO: add Timelines;
        EventTimeline eventTimeline = new EventTimeline(10);
        for (int i = 0; i < 10; i++) {
            float time = (i + 1) * 1;
            EventData eventData = new EventData("TestEventName");
            Event event = new Event(time, eventData, null);
            eventTimeline.setFrame(i, event);
        }
        timelines.add(eventTimeline);
        node = emitter.getNode();
    }

    @Override
    public void act(float delta) {
        state.update(delta);
        state.apply();
        applyTransform(delta);
        super.act(delta);
    }

    @Override
    public void applyTransform(float delta) {
        // 位移变换
        float speedX = data.speed * MathUtils.cosDeg(data.speedDirection);
        float speedY = data.speed * MathUtils.sinDeg(data.speedDirection);
        moveBy(speedX * delta, speedY * delta);
        /* ------------------------------------------------------------- */
        // 角度变换
        if (data.towardSameAsSpeedDirection) {
            setRotation(data.speedDirection);
        } else {
            setRotation(data.toward + node.getWorldRotationX());
        }
        /* ------------------------------------------------------------- */
        // 缩放变换
        float sclX = node.getWorldScaleX();
        float sclY = node.getWorldScaleY();
        setScale(sclX * data.scaleX, sclY * data.scaleY);
        /* ------------------------------------------------------------- */
        // 颜色变换
        setColor(data.color);
    }

    public void applyRotation(float delta) {

    }

    public void applyScale() {
    }
}
