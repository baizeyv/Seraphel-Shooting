package com.seraphel.shooting.master.builtin;

public class TrackItem {

    public Projectile projectile;

    public float delay;

    public float timeScale;

    public ProjectileStateListener listener;

    // @attribute trackTime 轨道的当前时间,表示弹幕已经播放的时间
    // @attribute trackLast 上次更新时的时间
    // @attribute trackEnd 轨道的结束时间,超过这个时候后弹幕会停止播放
    // @attribute nextTrackLast 下次更新时要赋给TrackLast的值
    public float trackTime, trackLast, trackEnd, nextTrackLast;

    // @attribute projectileStart 投射物的起始时间,通常为0. 在一些特殊情况下可以设置投射物从某个时间点开始而不是从头开始
    // @attribute projectileEnd 投射物的结束时间,用于控制投射物的播放长度
    // @attribute projectileLast 上次更新时的投射物时间
    // @attribute nextProjectileLast 下次更新时要赋给projectileLast的值
    public float projectileStart, projectileEnd, projectileLast, nextProjectileLast;

    public float getProjectileTime() {
        return Math.min(trackTime + projectileStart, projectileEnd);
    }

}
