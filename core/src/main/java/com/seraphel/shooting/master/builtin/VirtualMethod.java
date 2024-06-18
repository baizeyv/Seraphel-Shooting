package com.seraphel.shooting.master.builtin;

import com.badlogic.gdx.math.Vector2;

public interface VirtualMethod {

    /**
     * 获取自机位置
     * @return 自机位置
     */
    Vector2 getOtherPosition();

    /**
     * 获取自机角度
     * @return 自机角度
     */
    float getOtherRotation();

}
