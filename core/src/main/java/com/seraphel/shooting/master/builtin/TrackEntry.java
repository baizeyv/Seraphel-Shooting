package com.seraphel.shooting.master.builtin;

public class TrackEntry {

    public Barrage barrage;

    public float delay;

    public float timeScale;

    public boolean loop;

    public BarrageStateListener listener;

    // @attribute trackTime 轨道的当前时间,表示弹幕已经播放的时间
    // @attribute trackLast 上次更新时的时间
    // @attribute trackEnd 轨道的结束时间,超过这个时候后弹幕会停止播放
    // @attribute nextTrackLast 下次更新时要赋给TrackLast的值
    public float trackTime, trackLast, trackEnd, nextTrackLast;

    // @attribute barrageStart 弹幕的起始时间,通常为0. 在一些特殊情况下可以设置弹幕从某个时间点开始而不是从头开始
    // @attribute barrageEnd 弹幕的结束时间,用于控制弹幕的播放长度
    // @attribute barrageLast 上次更新时的弹幕时间
    // @attribute nextBarrageLast 下次更新时要赋给barrageLast的值
    public float barrageStart, barrageEnd, barrageLast, nextBarrageLast;

    public float getBarrageTime() {
        if (loop) {
            // 弹幕持续时间
            float duration = barrageEnd - barrageStart;
            if (duration == 0)
                return barrageStart;
            return (trackTime % duration) + barrageStart;
        }
        // 非循环弹幕
        return Math.min(trackTime + barrageStart, barrageEnd);
    }

}
