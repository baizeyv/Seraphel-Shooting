package com.seraphel.shooting.master.extend;

import com.seraphel.shooting.master.builtin.Launcher;
import com.seraphel.shooting.master.builtin.LauncherCollector;
import com.seraphel.shooting.master.extend.data.EmitterData;

public class Emitter implements Launcher {

    private final EmitterData data;

    private final LauncherCollector collector;

    private final String name;

    public Emitter(EmitterData data, LauncherCollector collector, String name) {
        this.data = data;
        this.collector = collector;
        this.name = name;
    }
}
