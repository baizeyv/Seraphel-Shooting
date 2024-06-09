package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.seraphel.shooting.master.builtin.enumerate.EventType;

public class EventQueue {

    private final BarrageState barrageState;

    private final Array objects = new Array();

    private boolean drainDisabled;

    public EventQueue(BarrageState barrageState) {
        this.barrageState = barrageState;
    }

    void start(TrackEntry entry) {
        objects.add(EventType.start);
        objects.add(entry);
    }

    void interrupt(TrackEntry entry) {
        objects.add(EventType.interrupt);
        objects.add(entry);
    }

    void pause(TrackEntry entry) {
        objects.add(EventType.pause);
        objects.add(entry);
    }

    void restore(TrackEntry entry) {
        objects.add(EventType.restore);
        objects.add(entry);
    }

    void end(TrackEntry entry) {
        objects.add(EventType.end);
        objects.add(entry);
    }

    void dispose(TrackEntry entry) {
        objects.add(EventType.dispose);
        objects.add(entry);
    }

    void complete(TrackEntry entry) {
        objects.add(EventType.complete);
        objects.add(entry);
    }

    void event(TrackEntry entry, Event event) {
        objects.add(EventType.event);
        objects.add(entry);
        objects.add(event);
    }

    void drain() {
        if (drainDisabled)
            return;
        drainDisabled = true;

        Array objects = this.objects;
        Array<BarrageStateListener> listeners = barrageState.listeners;
        for (int i = 0; i < objects.size; i += 2) {
            EventType type = (EventType) objects.get(i);
            TrackEntry entry = (TrackEntry) objects.get(i + 1);
            System.out.println(type.name() + " listener . " + TimeUtils.millis());
            switch (type) {
                case start:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).start(entry);
                    break;
                case interrupt:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).interrupt(entry);
                    break;
                case pause:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).pause(entry);
                    break;
                case restore:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).restore(entry);
                    break;
                case end:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).end(entry);
                    break;
                case dispose:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).dispose(entry);
                    break;
                case complete:
                    if (entry.listener != null)
                        entry.listener.start(entry);
                    for (int ii = 0; ii < listeners.size; ii ++)
                        listeners.get(ii).complete(entry);
                    break;
                case event:
                    Event event = (Event) objects.get(i++ + 2);
                    if (entry.listener != null)
                        entry.listener.event(entry, event);
                    for (int ii = 0; ii < listeners.size; ii ++)
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
