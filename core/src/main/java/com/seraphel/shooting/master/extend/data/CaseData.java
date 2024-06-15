package com.seraphel.shooting.master.extend.data;

import com.badlogic.gdx.utils.ArrayMap;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.extend.Emitter;
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
    public boolean detect(ArrayMap<PropertyType, Float> conditionMap, CaseGroupData caseGroup, Emitter emitter) {
        // TODO: args
        int aFlag = detectCondition(conditionMap, caseGroup, emitter, conditionA, operatorA, valueA);
        int bFlag = detectCondition(conditionMap, caseGroup, emitter, conditionB, operatorB, valueB);

        // 无条件
        if (aFlag == -1 && bFlag == -1) {
            return false;
        } else if (aFlag == -1) { // 只有B条件
            return bFlag > 0;
        } else if (bFlag == -1) { // 只有A条件
            return aFlag > 0;
        } else { // 有A也有B
            if (linkOperator == OperatorType.AND) {
                return aFlag + bFlag == 2;
            } else if (linkOperator == OperatorType.OR) {
                return aFlag + bFlag > 0;
            }
        }
        return false;
    }

    /**
     * 检测指定条件是否通过验证
     *
     * @param conditionMap
     * @param caseGroup
     * @param emitter
     * @param property
     * @param operator
     * @param value
     * @return -1 -> null 1->true 0->false
     */
    private int detectCondition(ArrayMap<PropertyType, Float> conditionMap, CaseGroupData caseGroup, Emitter emitter, PropertyType property, OperatorType operator, String value) {
        if (operator == OperatorType.GREATER_THAN) {
            if (property == PropertyType.CURRENT_FRAME) { // 条件A是当前帧
                float gap = gainDetectionGap(emitter);
                int va = Integer.parseInt(value);
                float conditionValue = va * gap;
                return conditionMap.get(PropertyType.CURRENT_FRAME) > conditionValue ? 1 : 0;
            } else {
                return conditionMap.get(property) > Float.parseFloat(value) ? 1 : 0;
            }
        } else if (operator == OperatorType.EQUAL_TO) {
            if (property == PropertyType.CURRENT_FRAME) {
                float gap = gainDetectionGap(emitter);
                int va = Integer.parseInt(value);
                float conditionValue = va * gap;
                return conditionMap.get(PropertyType.CURRENT_FRAME) == conditionValue ? 1 : 0;
            } else {
                return conditionMap.get(property) == Float.parseFloat(value) ? 1 : 0;
            }
        } else if (operator == OperatorType.LESS_THAN) {
            if (property == PropertyType.CURRENT_FRAME) {
                float gap = gainDetectionGap(emitter);
                int va = Integer.parseInt(value);
                float conditionValue = va * gap;
                return conditionMap.get(PropertyType.CURRENT_FRAME) < conditionValue ? 1 : 0;
            } else {
                return conditionMap.get(property) < Float.parseFloat(value) ? 1 : 0;
            }
        } else {
            // 空条件
            return -1;
        }
    }

    /**
     * 获取事件检测间隔
     */
    private float gainDetectionGap(Emitter emitter) {
        int detectionUnit = emitter.ref.detectionUnit;
        switch (detectionUnit) {
            case 60:
                return Constant.STANDARD_FRAME_TIME_60;
            default:
                return Constant.STANDARD_FRAME_TIME;
        }
    }

    public Object cloneX() throws CloneNotSupportedException {
        CaseData res = (CaseData) super.clone();
        res.curve = (CurveData) this.curve.cloneX();
        return res;
    }
}
