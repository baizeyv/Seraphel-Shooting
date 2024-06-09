package com.seraphel.shooting.master.builtin;

public interface ProjectileStateListener {

    public void start(TrackItem item);

    public void interrupt(TrackItem item);

    public void end(TrackItem item);

    public void dispose(TrackItem item);

    public void complete(TrackItem item);

    public void event(TrackItem item, Event event);

}
