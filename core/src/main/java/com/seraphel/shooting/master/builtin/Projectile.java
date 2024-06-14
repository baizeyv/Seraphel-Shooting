package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.seraphel.shooting.master.builtin.timeline.Timeline;

/**
 * 抛射物
 */
public interface Projectile {

    ArrayMap<TimelinePriority, Array<Timeline>> timelines();

    void applyTransform(float delta);

    float duration();

}
