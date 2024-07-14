package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.seraphel.shooting.constant.Log;
import com.seraphel.shooting.master.builtin.data.BarrageStateData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;
import com.seraphel.shooting.master.builtin.timeline.Timeline;

public class BarrageState {

    /**
     * default empty barrage
     */
    private static final Barrage emptyBarrage = new Barrage("<empty>", new ArrayMap<>(), 0, new ArrayMap<>());

    private BarrageStateData data;

    private TrackEntry track;

    private final Array<Event> events = new Array<Event>();

    public final Array<BarrageStateListener> listeners = new Array<BarrageStateListener>();

    private final EventQueue queue = new EventQueue(this);

    private float timeScale = 1;

    private Barrage currentBarrage;

    public BarrageState(BarrageStateData data) {
        if (data == null)
            throw new IllegalArgumentException("Barrage state data cannot be null");
        this.data = data;
    }

    public void update(float delta) {
        delta *= timeScale;
        TrackEntry current = track;
        if (current != null) {
            current.barrageLast = current.nextBarrageLast;
            current.trackLast = current.nextTrackLast;

            float currentDelta = delta * current.timeScale;

            if (current.delay > 0) {
                current.delay -= currentDelta;
                if (current.delay > 0)
                    return;
                currentDelta = -current.delay;
                current.delay = 0;
            }
            if (current.trackLast >= current.trackEnd) {
                // dispose
                queue.end(current);
            } else {
                current.trackTime += currentDelta;
            }
        }
        queue.drain();
    }

    public void apply() {
        Array<Event> events = this.events;
        TrackEntry current = track;
        if (current == null || current.delay > 0) {
        } else {
            // @attribute barrageLast 上次更新时的弹幕时间
            // @attribute barrageTime 当前弹幕的播放时间
            float barrageLast = current.barrageLast, barrageTime = current.getBarrageTime();
            if (current.barrage.timelines.containsKey(TimelinePriority.FIRSTLY)) {
                for (Timeline timeline : current.barrage.timelines.get(TimelinePriority.FIRSTLY)) {
                    if (timeline instanceof EventTimeline) {
                        ((EventTimeline) timeline).setFiredEvents(events);
                        timeline.call(barrageLast, barrageTime);
                        events = ((EventTimeline) timeline).getFiredEvents();
                    } else {
                        timeline.call(barrageLast, barrageTime);
                    }
                }
            }
            if (current.barrage.timelines.containsKey(TimelinePriority.MIDDLE)) {
                for (Timeline timeline : current.barrage.timelines.get(TimelinePriority.MIDDLE)) {
                    if (timeline instanceof EventTimeline) {
                        ((EventTimeline) timeline).setFiredEvents(events);
                        timeline.call(barrageLast, barrageTime);
                        events = ((EventTimeline) timeline).getFiredEvents();
                    } else {
                        timeline.call(barrageLast, barrageTime);
                    }
                }
            }
            if (current.barrage.timelines.containsKey(TimelinePriority.FINALLY)) {
                for (Timeline timeline : current.barrage.timelines.get(TimelinePriority.FINALLY)) {
                    if (timeline instanceof EventTimeline) {
                        ((EventTimeline) timeline).setFiredEvents(events);
                        timeline.call(barrageLast, barrageTime);
                        events = ((EventTimeline) timeline).getFiredEvents();
                    } else {
                        timeline.call(barrageLast, barrageTime);
                    }
                }
            }
            if (current.barrage.timelines.containsKey(TimelinePriority.BUILTIN_EVENT)) {
                for (Timeline timeline : current.barrage.timelines.get(TimelinePriority.BUILTIN_EVENT)) {
                    if (timeline instanceof EventTimeline) {
                        ((EventTimeline) timeline).setFiredEvents(events);
                        timeline.call(barrageLast, barrageTime);
                        events = ((EventTimeline) timeline).getFiredEvents();
                    } else {
                        timeline.call(barrageLast, barrageTime);
                    }
                }
            }
            queueEvents(current, barrageTime);
            events.clear();
            current.nextBarrageLast = barrageTime;
            current.nextTrackLast = current.trackTime;
        }
        queue.drain();
    }

    private void queueEvents(TrackEntry entry, float barrageTime) {
        float barrageStart = entry.barrageStart, barrageEnd = entry.barrageEnd;
        float duration = barrageEnd - barrageStart;
        float trackLastWrapped = entry.trackLast % duration;

        // Queue events before complete.
        Array<Event> events = this.events;
        int i = 0, n = events.size;
        for (; i < n; i++) {
            Event event = events.get(i);
            if (event.time < trackLastWrapped)
                break;
            if (event.time > barrageEnd)
                continue;
            queue.event(entry, event);
        }

        boolean complete;
        if (entry.loop)
            complete = duration == 0 || trackLastWrapped > entry.trackTime % duration;
        else
            complete = barrageTime >= barrageEnd && entry.barrageLast < barrageEnd;
        if (complete) {
            queue.complete(entry);
            // TODO: 恢复所有Emitter的初始状态
            for (ObjectMap.Entry<String, ArrayMap<String, Launcher>> item : currentBarrage.collectorLaunchers) {
                for (ObjectMap.Entry<String, Launcher> ety : item.value) {
                    Log.error(item.key + " - " + ety.key);
                    ety.value.reset();
                }
            }
            // 清除由Case创造出的时间轴
            if (track.barrage.timelines.containsKey(TimelinePriority.MIDDLE)) {
                track.barrage.timelines.get(TimelinePriority.MIDDLE).clear();
            }
            // 恢复所有时间轴的初始状态
            for (ObjectMap.Entry<TimelinePriority, Array<Timeline>> timelineEntry : track.barrage.timelines) {
                if (timelineEntry.value == null)
                    continue;
                for (Timeline timeline : timelineEntry.value) {
                    timeline.reset();
                }
            }
        }

        // Queue events after complete.
        for (; i < n; i++) {
            Event event = events.get(i);
            if (event.time < barrageStart)
                continue;
            queue.event(entry, events.get(i));
        }
    }

    public TrackEntry play(String name, boolean loop) {
        Barrage barrage = data.nodeTreeData.findBarrage(name);
        if (barrage == null)
            throw new IllegalArgumentException("Barrage " + name + " not found");
        return play(barrage, loop);
    }

    public TrackEntry play(Barrage barrage, boolean loop) {
        if (barrage == null)
            throw new IllegalArgumentException("Barrage cannot be null");
        currentBarrage = barrage;
        TrackEntry current = track;
        if (current != null) {
            queue.interrupt(current);
            queue.end(current);
        }
        TrackEntry entry = trackEntry(barrage, loop);
        queue.start(entry);
        this.track = entry;
        queue.drain();
        barrage.bindLauncher();
        return entry;
    }

    private TrackEntry trackEntry(Barrage barrage, boolean loop) {
        TrackEntry entry = new TrackEntry();
        entry.barrage = barrage;
        entry.delay = 0;
        entry.timeScale = 1;
        entry.loop = loop;

        entry.trackTime = 0;
        entry.trackLast = -1;
        entry.nextTrackLast = -1;
        entry.trackEnd = Float.MAX_VALUE;

        entry.barrageStart = 0;
        entry.barrageEnd = barrage.getDuration();
        entry.barrageLast = -1;
        entry.nextBarrageLast = -1;
        return entry;
    }

}
