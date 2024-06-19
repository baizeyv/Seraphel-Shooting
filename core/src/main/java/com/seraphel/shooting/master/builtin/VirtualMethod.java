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

    /**
     * 获取自身对象的位置偏移量 (相对于 Stage)
     * @return 位置偏移量
     */
    Vector2 getOffsetPosition();

    /**
     * 获取自身对象的角度偏移量 (相对于 Stage)
     * @return 角度偏移量
     */
    float getOffsetRotation();

    /**
     * 获取自身对象的缩放偏移量 (相对于 Stage)
     * @return 缩放偏移量
     */
    Vector2 getOffsetScale();

}
