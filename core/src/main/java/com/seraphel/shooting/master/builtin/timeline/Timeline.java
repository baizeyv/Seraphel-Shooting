package com.seraphel.shooting.master.builtin.timeline;

public interface Timeline {

    public void call(float lastTime, float time);

    public int getPropertyId();

    public void reset();

    public int getFrameCount();

    public float[] getFrames();

}
