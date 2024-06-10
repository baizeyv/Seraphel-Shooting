package com.seraphel.shooting.master.builtin;

import com.seraphel.shooting.master.builtin.data.EventData;

public class Event {

    private final EventData data;

    public final float time;

    /**
     * 事件默认执行器, 在所有listener之前执行
     */
    private final Executable executable;

    public Event(float time, EventData data, Executable executable) {
        if (data == null)
            throw new IllegalArgumentException("data cannot be null");
        this.data = data;
        this.time = time;
        this.executable = executable;
    }

    public void execute() {
        if (executable != null)
            executable.execute();
    }

    public String getName() {
        return data.name;
    }
}
