package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
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
     * key -> 发射器收集者名称
     * value -> 此时的所有时间轴
     */
    public ArrayMap<String, Array<Timeline>> timelineMap;

    /**
     * 当前弹幕的持续时间
     */
    private final float duration;

    public Barrage(String name, ArrayMap<String, Array<Timeline>> timelineMap, float duration) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.timelineMap = timelineMap;
        this.duration = duration;
        if (timelineMap.size > 0)
            setTimelines(timelineMap.getValueAt(0)); // 默认使用第一个发射器收集者
    }

    public void setTimelines(Array<Timeline> timelines) {
        if (timelines == null)
            throw new IllegalArgumentException("timelines cannot be null");
        this.timelines = timelines;
    }

    public float getDuration() {
        return duration;
    }
}
