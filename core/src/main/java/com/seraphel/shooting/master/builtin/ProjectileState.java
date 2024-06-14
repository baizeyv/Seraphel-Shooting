package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.data.ProjectileData;
import com.seraphel.shooting.master.builtin.timeline.EventTimeline;
import com.seraphel.shooting.master.builtin.timeline.Timeline;

public class ProjectileState {

    public final Array<ProjectileStateListener> listeners = new Array<ProjectileStateListener>();
    private final Array<Event> events = new Array<Event>();
    private final ProjectileEventQueue queue = new ProjectileEventQueue(this);
    private ProjectileData data;
    private TrackItem track;
    private float timeScale = 1;

    public ProjectileState(ProjectileData data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
        this.data = data;
    }

    public void update(float delta) {
        delta *= timeScale;
        TrackItem current = track;
        if (current != null) {
            current.projectileLast = current.nextProjectileLast;
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
        TrackItem current = track;
        if (current == null || current.delay > 0) {

        } else {
            float projectileLast = current.projectileLast, projectileTime = current.getProjectileTime();
            for (Timeline timeline : current.projectile.timelines()) {
                if (timeline instanceof EventTimeline) {
                    ((EventTimeline) timeline).setFiredEvents(events);
                    timeline.call(projectileLast, projectileTime);
                    events = ((EventTimeline) timeline).getFiredEvents();
                } else {
                    timeline.call(projectileLast, projectileTime);
                }
            }
            queueEvents(current, projectileTime);
            events.clear();
            current.nextProjectileLast = projectileTime;
            current.nextTrackLast = current.trackTime;
        }
        queue.drain();
    }

    public void queueEvents(TrackItem item, float projectileTime) {
        float projectileStart = item.projectileStart, projectileEnd = item.projectileEnd;
        float duration = projectileEnd - projectileStart;
        float trackLastWrapped = item.trackLast % duration;

        // Queue events before complete.
        Array<Event> events = this.events;
        int i = 0, n = events.size;
        for (; i < n; i++) {
            Event event = events.get(i);
            if (event.time < trackLastWrapped)
                break;
            if (event.time > projectileEnd)
                continue;
            queue.event(item, event);
        }

        boolean complete;
        complete = projectileTime >= projectileEnd && item.projectileLast < projectileEnd;
        if (complete) {
            queue.complete(item);
        }

        // Queue events after complete.
        for (; i < n; i++) {
            Event event = events.get(i);
            if (event.time < projectileStart)
                continue;
            queue.event(item, events.get(i));
        }
    }

    public TrackItem play(Projectile projectile) {
        if (projectile == null)
            throw new IllegalArgumentException("projectile cannot be null");
        TrackItem current = track;
        if (current != null) {
            queue.interrupt(current);
            queue.end(current);
        }
        TrackItem item = trackItem(projectile);
        queue.start(item);
        this.track = item;
        queue.drain();
        return item;
    }

    private TrackItem trackItem(Projectile projectile) {
        TrackItem item = new TrackItem();
        item.projectile = projectile;
        item.delay = 0;
        item.timeScale = 1;

        item.trackTime = 0;
        item.trackLast = -1;
        item.nextTrackLast = -1;
        item.trackEnd = Float.MAX_VALUE;

        item.projectileStart = 0;
        item.projectileEnd = projectile.duration();
        item.projectileLast = -1;
        item.nextProjectileLast = -1;
        return item;
    }

    public void updateTrack(float duration) {
        if (track != null)
            track.projectileEnd = duration;
    }

}
