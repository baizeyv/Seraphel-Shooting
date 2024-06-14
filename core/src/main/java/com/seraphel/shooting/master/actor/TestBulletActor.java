package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.actor.SpriteActor;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.builtin.*;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;
import com.seraphel.shooting.master.builtin.timeline.Timeline;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.BulletData;

public class TestBulletActor extends SpriteActor implements Projectile {

    BulletData data;

    ProjectileState state;

    private Node node;

    // TODO: 换成和发射器相同的具有优先级分别的MAP
    private final Array<Timeline> timelines = new Array<>();

    private final Vector2 speed = new Vector2();

    public TestBulletActor(String atlasPath, String regionName, BulletData data, Emitter emitter) {
        super(atlasPath, regionName);
        this.data = data;
        this.state = new ProjectileState(data);
        node = emitter.getNode();

        /* ------------------------------------------------------------------------------------------- */
        // 生命周期事件时间轴
        Executable lifeActuator = new Executable() {
            @Override
            public void execute() {
                lifeEnd();
            }
        };
        EventTimeline lifeTimeline = new EventTimeline(1);
        EventData lifeEventData = new EventData("LifeEvent-" + data.life);
        float lifeTime = data.life * Constant.STANDARD_FRAME_TIME;
        Event lifeEvent = new Event(lifeTime, lifeEventData, lifeActuator);
        lifeTimeline.setFrame(0, lifeEvent);
        /* ------------------------------------------------------------------------------------------- */
        timelines.add(lifeTimeline);

        state.play(this);
        /* ------------------------------------------------------------------------------------------- */
        speed.x = data.speed * MathUtils.cosDeg(data.speedDirection) * data.horizontalRatio;
        speed.y = data.speed * MathUtils.sinDeg(data.speedDirection) * data.verticalRatio;
    }

    @Override
    public void act(float delta) {
        state.updateTrack(duration());
        state.update(delta);
        state.apply();
        applyTransform(delta);
        super.act(delta);
    }

    @Override
    public Array<Timeline> timelines() {
        return timelines;
    }

    @Override
    public void applyTransform(float delta) {
        // 位移变换
        float accelerationX = data.acceleration * MathUtils.cosDeg(data.accelerationDirection) * data.horizontalRatio;
        float accelerationY = data.acceleration * MathUtils.sinDeg(data.accelerationDirection) * data.verticalRatio;
        speed.x += accelerationX * (delta / Constant.STANDARD_FRAME_TIME);
        speed.y += accelerationY * (delta / Constant.STANDARD_FRAME_TIME);
        moveBy(speed.x * delta, speed.y * delta);
        /* ------------------------------------------------------------- */
        // 角度变换
        applyRotation();
        /* ------------------------------------------------------------- */
        // 缩放变换
        applyScale();
        /* ------------------------------------------------------------- */
        // 颜色变换
        applyColor();
    }

    @Override
    public float duration() {
        return data.life * Constant.STANDARD_FRAME_TIME;
    }

    private void lifeEnd() {
        remove();
    }

    public void applyRotation() {
        if (data.towardSameAsSpeedDirection) {
            setRotation(speed.angleDeg());
        } else {
            setRotation(data.toward + node.getWorldRotationX());
        }
    }

    public void applyScale() {
        float sclX = node.getWorldScaleX();
        float sclY = node.getWorldScaleY();
        setScale(sclX * data.scaleX, sclY * data.scaleY);
    }

    public void applyColor() {
        setColor(data.color);
    }
}
