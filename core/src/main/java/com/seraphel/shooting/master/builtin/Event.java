package com.seraphel.shooting.master.builtin;

import com.seraphel.shooting.master.builtin.data.EventData;

public class Event {

    private final EventData data;

    public final float time;

    public Event(float time, EventData data) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
        this.data = data;
        this.time = time;
    }
}
