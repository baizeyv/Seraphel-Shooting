package com.seraphel.shooting.master.extend;

import com.seraphel.shooting.master.builtin.Launcher;
import com.seraphel.shooting.master.builtin.LauncherCollector;
import com.seraphel.shooting.master.extend.data.EmitterData;

public class Emitter implements Launcher {

    /* 引用数据,可修改 */
    public final EmitterData ref;
    /* 源数据,不可修改 */
    private final EmitterData data;

    private final LauncherCollector collector;

    private final String name;

    public Emitter(EmitterData data, LauncherCollector collector, String name) throws CloneNotSupportedException {
        this.data = data;
        this.ref = (EmitterData) this.data.cloneX();
        this.collector = collector;
        this.name = name;
    }

    /**
     * 发射弹幕
     */
    public void shoot() {

    }
}
