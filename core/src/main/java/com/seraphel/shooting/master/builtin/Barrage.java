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
    public ArrayMap<TimelinePriority, Array<Timeline>> timelines;

    /**
     * key -> Launcher Collector
     * value -> <Timeline Priority, Current All Timelines>
     */
    private ArrayMap<String, ArrayMap<TimelinePriority, Array<Timeline>>> timelineMap;

    /**
     * 当前弹幕的持续时间
     */
    private final float duration;

    public Barrage(String name, ArrayMap<String, ArrayMap<TimelinePriority, Array<Timeline>>> timelineMap, float duration) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.timelineMap = timelineMap;
        this.duration = duration;
        if (timelineMap.size > 0)
            setTimelines(timelineMap.getValueAt(0)); // 默认使用第一个发射器收集者
    }

    public void setTimelines(ArrayMap<TimelinePriority, Array<Timeline>> timelines) {
        if (timelines == null)
            throw new IllegalArgumentException("timelines cannot be null");
        this.timelines = timelines;
    }

    public float getDuration() {
        return duration;
    }
}
