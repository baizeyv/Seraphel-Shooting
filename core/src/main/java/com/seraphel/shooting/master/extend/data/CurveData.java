package com.seraphel.shooting.master.extend.data;

public class CurveData implements Cloneable {

    public float time;

    public float curveStartX;

    public float curveStartY;

    public float curveEndX;

    public float curveEndY;

    public Object cloneX() throws CloneNotSupportedException {
        return super.clone();
    }
}
