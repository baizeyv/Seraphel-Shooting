package com.seraphel.shooting.master.extend.data;

public class CurveData implements Cloneable {

    /**
     * 0 -> FIXED 固定变化
     * 1 -> PROPORTIONAL 正比变化
     * 2 -> SIN 正弦变化
     * 3 -> BASIC CURVE 基础曲线变化
     * 4 -> ADVANCE CURVE 高级曲线变化
     */
    public int type;

    /* ------------------------------------------------------ */
    // BASIC CURVE ARGUMENTS

    public float time;

    public float curveStartX;

    public float curveStartY;

    public float curveEndX;

    public float curveEndY;

    /* ------------------------------------------------------ */
    // ADVANCE CURVE ARGUMENTS
    // TODO: 应当是数组形式

    public int phaseCount;

    public float timeBegin;

    public float valueBegin;

    public float outTangent;

    public float outWeight;

    public float timeEnd;

    public float valueEnd;

    public float inTangent;

    public float inWeight;
    /* ------------------------------------------------------ */

    public Object cloneX() throws CloneNotSupportedException {
        return super.clone();
    }
}
