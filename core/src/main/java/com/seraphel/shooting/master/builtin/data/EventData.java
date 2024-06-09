package com.seraphel.shooting.master.builtin.data;

public class EventData {

    /**
     * event name
     */
    public final String name;

    public EventData(String name) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
    }
}
