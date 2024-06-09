package com.seraphel.shooting.master.builtin.timeline;

public interface Timeline {

    public void call(float lastTime, float time);

    /**
     * 验证complete触发时是否缺少执行一些东西,此时补上执行
     * @param lastTime
     * @param time
     * @param loop 当前 track 是否循环
     */
    public void verify(float lastTime, float time, boolean loop);

    public void restore();

    public int getPropertyId();

    public int getFrameCount();

    public float[] getFrames();

}
