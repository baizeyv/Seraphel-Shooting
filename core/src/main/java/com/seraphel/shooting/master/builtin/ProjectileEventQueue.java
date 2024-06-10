package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.seraphel.shooting.master.builtin.enumerate.EventType;

public class ProjectileEventQueue {

    private final ProjectileState projectileState;

    private final Array objects = new Array();

    private boolean drainDisabled;

    public ProjectileEventQueue(ProjectileState projectileState) {
        this.projectileState = projectileState;
    }

    public void start(TrackItem item) {
        objects.add(EventType.start);
        objects.add(item);
    }

    public void interrupt(TrackItem item) {
        objects.add(EventType.interrupt);
        objects.add(item);
    }

    public void end(TrackItem item) {
        objects.add(EventType.end);
        objects.add(item);
    }

    public void dispose(TrackItem item) {
        objects.add(EventType.dispose);
        objects.add(item);
    }

    public void complete(TrackItem item) {
        objects.add(EventType.complete);
        objects.add(item);
    }

    public void event(TrackItem item, Event event) {
        objects.add(EventType.event);
        objects.add(item);
        objects.add(event);
    }

    public void drain() {
        if (drainDisabled)
            return;
        drainDisabled = true;

        Array objects = this.objects;
        Array<ProjectileStateListener> listeners = this.projectileState.listeners;
        for (int i = 0; i < objects.size; i += 2) {
            EventType type = (EventType) objects.get(i);
            TrackItem entry = (TrackItem) objects.get(i + 1);
            System.out.println(type.name() + " TrackItem listener . " + TimeUtils.millis());
            switch (type) {
                case start:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii++)
                        listeners.get(ii).start(entry);
                    break;
                case interrupt:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii++)
                        listeners.get(ii).interrupt(entry);
                    break;
                case end:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii++)
                        listeners.get(ii).end(entry);
                    break;
                case dispose:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii++)
                        listeners.get(ii).dispose(entry);
                    break;
                case complete:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii++)
                        listeners.get(ii).complete(entry);
                    break;
                case event:
                    Event event = (Event) objects.get(i++ + 2);
                    if (entry.listener != null)
                        entry.listener.event(entry, event);
                    for (int ii = 0; ii < listeners.size; ii++)
                        listeners.get(ii).event(entry, event);
                    break;
            }
        }
        clear();
        drainDisabled = false;
    }

    void clear() {
        objects.clear();
    }

}
