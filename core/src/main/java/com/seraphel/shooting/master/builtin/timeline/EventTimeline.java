package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.Event;
import com.seraphel.shooting.master.Utils;

public class EventTimeline extends BaseTimeline {

    private final Event[] events;

    private Array<Event> firedEvents;

    public EventTimeline(int frameCount) {
        super(frameCount);
        events = new Event[frameCount];
    }

    public void setFrame(int frameIndex, Event event) {
        frames[frameIndex] = event.time;
        originFrames[frameIndex] = event.time;
        events[frameIndex] = event;
    }

    /**
     * 这个时间轴在调用之前需要先调用 setFiredEvents, 调用后需要取出 getFiredEvents 返回赋值
     * @param lastTime
     * @param time
     */
    @Override
    public void call(float lastTime, float time) {
        super.call(lastTime, time);
        if (firedEvents == null)
            return;
        float[] frames = this.frames;
        int frameCount = frames.length;
        if (lastTime > time) { // fire events after last time for looped animations
            call(lastTime, Integer.MAX_VALUE);
            lastTime = -1f;
        } else if (lastTime >= frames[frameCount - 1]) { // last time is after last frame
            return;
        }
        if (time < frames[0]) // time is before first frame
            return;

        int frame;
        if (lastTime < frames[0])
            frame = 0;
        else {
            frame = Utils.binarySearch(frames, lastTime);
            float frameTime = frames[frame];
            while (frame > 0) { // fire multiple events with the same frame
                if (frames[frame - 1] != frameTime)
                    break;
                frame --;
            }
        }
        for(; frame < frameCount && time >= frames[frame]; frame++) {
            firedEvents.add(events[frame]);
        }
    }

    public void setFiredEvents(Array<Event> firedEvents) {
        this.firedEvents = firedEvents;
    }

    public Array<Event> getFiredEvents() {
        return firedEvents;
    }
}
