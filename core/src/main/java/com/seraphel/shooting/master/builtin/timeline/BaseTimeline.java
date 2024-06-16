package com.seraphel.shooting.master.builtin.timeline;

public class BaseTimeline implements Timeline {

    /**
     * 真正使用的数组, 在使用过程中可能会更改他的值
     */
    protected float[] frames;

    /**
     * 存储数组原状的数组
     */
    protected float[] originFrames;

    public BaseTimeline(int frameCount) {
        if (frameCount <= 0)
            throw new IllegalArgumentException("frameCount must be greater than 0: " + frameCount);
        frames = new float[frameCount];
        originFrames = new float[frameCount];
    }

    @Override
    public void call(float lastTime, float time) {

    }

    @Override
    public void reset() {
        System.arraycopy(originFrames, 0, frames, 0, frames.length);
    }

    @Override
    public int getPropertyId() {
        return 0;
    }

    /**
     * get the frame array length
     * @return the number of key frames for this timeline
     */
    @Override
    public int getFrameCount() {
        return frames.length;
    }

    /**
     * get frames array
     * @return the time in seconds for each key frames.
     */
    @Override
    public float[] getFrames() {
        return frames;
    }
}
