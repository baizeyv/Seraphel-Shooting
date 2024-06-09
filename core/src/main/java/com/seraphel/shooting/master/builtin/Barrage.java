package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.timeline.Timeline;

public class Barrage {

    /**
     * 弹幕名称
     */
    public final String name;

    /**
     * 当前弹幕存在的时间轴
     */
    public Array<Timeline> timelines;

    /**
     * 当前弹幕的持续时间
     */
    public float duration;

    public Barrage(String name, Array<Timeline> timelines, float duration) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.duration = duration;
        setTimelines(timelines);
    }

    public void setTimelines(Array<Timeline> timelines) {
        if (timelines == null)
            throw new IllegalArgumentException("timelines cannot be null");
        this.timelines = timelines;
    }
}
