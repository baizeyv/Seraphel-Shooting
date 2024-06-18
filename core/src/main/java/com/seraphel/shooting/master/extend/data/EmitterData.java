package com.seraphel.shooting.master.extend.data;

import com.badlogic.gdx.utils.Array;

public class EmitterData implements Cloneable {

    public int id;

    public int layerId;

    /* 条件检测的单位, for example: 60 -> 1/60s 处理一次 */
    public int detectionUnit;

    /* 绑定到的发射器的ID, 这种形式的绑定相当于在bindToId的发射器发射的子弹上都放一个当前的发射器 */
    public int bindToId;

    /* 深度绑定,当该发射器绑定到其他发射器时进行校验 */
    public boolean deepBind;

    /* 相对于绑定到的发射器的发射角度再加上自己的角度才是world中真正的发射角度 */
    public boolean relativeDirection;

    /* 位置 x 坐标 */
    public float x;

    /* 位置 y 坐标 */
    public float y;

    /* -------------------- split line -------------------- */

    /* unit: frame */
    public int startTime;

    /* unit: frame */
    public int endTime; // 需要根据持续时间换算

    /* -------------------- split line -------------------- */

    /* 发射 x 坐标 */
    public float shootX;

    /* 发射x坐标随机增量 */
    public float rdShootX;

    /* 发射 y 坐标 */
    public float shootY;

    /* 发射y坐标随机增量 */
    public float rdShootY;

    /* 发射半径 */
    public float radius;

    /* 发射半径随机增量 */
    public float rdRadius;

    /* 半径方向 */
    public float radiusDegree;

    /* 半径方向随机增量 */
    public float rdRadiusDegree;

    /* 条数 */
    public int count;

    /* 条数随机增量 */
    public int rdCount;

    /* unit: frame 周期 */
    public int cycle;

    /* 周期随机增量 */
    public int rdCycle;

    /* 发射角度 */
    public float angle;

    /* 角度随机增量 */
    public float rdAngle;

    /* 发射范围 */
    public float range;

    /* 范围随机增量 */
    public float rdRange;

    /* -------------------- split line -------------------- */

    /* 运动速度 */
    public float speed;

    /* 速度随机增量 */
    public float rdSpeed;

    /* 运动速度方向 */
    public float speedDirection;

    /* 速度方向随机增量 */
    public float rdSpeedDirection;

    /* 运动加速度 */
    public float acceleration;

    /* 加速度随机增量 */
    public float rdAcceleration;

    /* 运动加速度方向 */
    public float accelerationDirection;

    /* 加速度方向随机增量 */
    public float rdAccelerationDirection;

    /* -------------------- split line -------------------- */

    public BulletData bulletData;

    /* -------------------- split line -------------------- */

    /* 出屏即消 */
    public boolean removeOutOfScreen;

    /* 消弹效果 */
    public boolean disappearEffect;

    /* 拖影效果 */
    public boolean tailEffect;

    /* -------------------- split line -------------------- */

    /* 发射器的角度为自机的时候会使用 */
    public float specialAngle;

    /* -------------------- split line -------------------- */

    public Array<CaseGroupData> caseGroups = new Array<CaseGroupData>();

    public Object cloneX() throws CloneNotSupportedException {
        EmitterData res = (EmitterData) super.clone();
        res.bulletData = (BulletData) bulletData.cloneX();
        res.caseGroups = new Array<CaseGroupData>();
        for (int i = 0; i < this.caseGroups.size; i++) {
            CaseGroupData caseGroupData = this.caseGroups.get(i);
            res.caseGroups.add((CaseGroupData) caseGroupData.cloneX());
        }
        return res;
    }

}
