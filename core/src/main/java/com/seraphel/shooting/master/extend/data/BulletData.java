package com.seraphel.shooting.master.extend.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class BulletData {

    /* 生命 unit: frame */
    public int life;

    /* 子弹类型 ID */
    public int type;

    /* 宽比 */
    public float scaleX;

    /* 高比 */
    public float scaleY;

    /* 颜色 */
    public Color color;

    /* 朝向 */
    public float toward;

    /* 朝向随机增量 */
    public float rdToward;

    /* 朝向与速度方向相同 */
    public boolean towardSameAsSpeedDirection;

    /* -------------------- split line -------------------- */

    /* 速度 */
    public float speed;

    /* 速度随机增量 */
    public float rdSpeed;

    /* 加速度 */
    public float acceleration;

    /* 加速度随机增量 */
    public float rdAcceleration;

    /* 加速度方向 */
    public float accelerationDirection;

    /* 加速度方向随机增量 */
    public float rdAccelerationDirection;

    /* 横比 */
    public float horizontalRatio;

    /* 纵比 */
    public float verticalRatio;

    /* -------------------- split line -------------------- */

    public final Array<CaseGroupData> caseGroups = new Array<CaseGroupData>();

}
