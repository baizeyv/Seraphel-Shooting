package com.seraphel.shooting.master.builtin;

public interface BarrageStateListener {

    public void start(TrackEntry entry);

    public void interrupt(TrackEntry entry);

    public void pause(TrackEntry entry);

    public void restore(TrackEntry entry);

    public void end(TrackEntry entry);

    public void dispose(TrackEntry entry);

    public void complete(TrackEntry entry);

    public void event(TrackEntry entry, Event event);

}
