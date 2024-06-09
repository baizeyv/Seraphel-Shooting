package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.seraphel.shooting.master.builtin.timeline.Timeline;

/**
 * 抛射物
 */
public interface Projectile {

    public Array<Timeline> timelines = new Array<Timeline>();

    public float duration = 0;

}
