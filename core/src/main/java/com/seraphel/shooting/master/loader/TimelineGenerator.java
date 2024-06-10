package com.seraphel.shooting.master.loader;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.builtin.Event;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;
import com.seraphel.shooting.master.builtin.timeline.Timeline;
import com.seraphel.shooting.master.extend.Emitter;

/**
 * 时间轴生成器
 */
public class TimelineGenerator {
    // TODO

    public static Array<Timeline> emitter(Emitter emitter) {
        Array<Timeline> res = new Array<Timeline>();

        // 射击时间轴 [Shoot Timeline] 应当放在最后
        int shootFrameCount = (emitter.ref.endTime - emitter.ref.startTime) / emitter.ref.cycle;
        EventTimeline shootTimeline = new EventTimeline(shootFrameCount);
        for (int i = emitter.ref.startTime + emitter.ref.cycle, j = 0; i <= emitter.ref.endTime; i += emitter.ref.cycle, j++) {
            shootTimeline.setFrame(j, new Event(i * Constant.STANDARD_FRAME_TIME, new EventData("ShootEvent-" + (j + 1)), emitter.shootActuator));
        }
        res.add(shootTimeline);
        return res;
    }

    public static Timeline builtinEvent(JsonValue map, NodeTreeData nodeTreeData) {
        EventTimeline timeline = new EventTimeline(map.size);
        int frameIndex = 0;
        for (JsonValue eventMap = map.child; eventMap != null; eventMap = eventMap.next) {
            EventData eventData = nodeTreeData.findEvent(eventMap.getString("name"));
            if (eventData == null)
                throw new SerializationException("event not found");
            Event event = new Event(eventMap.getFloat("time", 0), eventData, null);
            timeline.setFrame(frameIndex++, event);
        }
        return timeline;
    }

}
