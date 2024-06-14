package com.seraphel.shooting.master.extend.data;

import com.seraphel.shooting.master.extend.enumerate.ChangeType;
import com.seraphel.shooting.master.extend.enumerate.OperatorType;
import com.seraphel.shooting.master.extend.enumerate.PropertyType;

public class CaseData implements Cloneable {

    public PropertyType conditionA;

    public OperatorType operatorA;

    public String valueA;

    public OperatorType linkOperator;

    public PropertyType conditionB;

    public OperatorType operatorB;

    public String valueB;

    public PropertyType propertyResult;

    public String resultValue;

    /* unit: frame */
    public int duration;

    public CurveData curve;

    public ChangeType changeType;

    /* 事件执行次数, 0代表无限 */
    public int loopTimes;

    public boolean specialShoot;

    public boolean specialRestore;

    /**
     * 检测是否达到条件
     * @return boolean
     */
    public boolean detect() {
        // TODO: args
        return false;
    }

    public Object cloneX() throws CloneNotSupportedException {
        CaseData res = (CaseData) super.clone();
        res.curve = (CurveData) this.curve.cloneX();
        return res;
    }
}
