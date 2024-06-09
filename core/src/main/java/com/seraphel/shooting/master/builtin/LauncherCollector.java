package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.OrderedMap;

public class LauncherCollector {

    public final String name;

    public final OrderedMap<LauncherEntry, LauncherEntry> launchers = new OrderedMap<LauncherEntry, LauncherEntry>();

    private final LauncherEntry lookup = new LauncherEntry();

    public LauncherCollector(String name) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.launchers.orderedKeys().ordered = false;
    }

    public void setLauncher(int pipeIndex, String name, Launcher launcher) {
        if (pipeIndex < 0)
            throw new IllegalArgumentException("pipeIndex cannot be negative");
        if (launcher == null)
            throw new IllegalArgumentException("component cannot be null");
        LauncherEntry newEntry = new LauncherEntry(pipeIndex, name, launcher);
        LauncherEntry oldEntry = launchers.put(newEntry, newEntry);
        if (oldEntry != null)
            oldEntry.launcher = launcher;
    }

    public Launcher getLauncher(int pipeIndex, String name) {
        if (pipeIndex < 0)
            throw new IllegalArgumentException("pipeIndex cannot be negative");
        lookup.set(pipeIndex, name);
        LauncherEntry entry = launchers.get(lookup);
        return entry != null ? entry.launcher : null;
    }

    public static class LauncherEntry {
        int pipeIndex;
        String name;
        Launcher launcher;

        public LauncherEntry() {
            set(0, "");
        }

        public LauncherEntry(int pipeIndex, String name, Launcher launcher) {
            set(pipeIndex, name);
            this.launcher = launcher;
        }

        void set(int pipeIndex, String name) {
            if (name == null)
                throw new IllegalArgumentException("name cannot be null");
            this.pipeIndex = pipeIndex;
            this.name = name;
        }
    }

}
