package com.seraphel.shooting.master.extend.data;

import com.badlogic.gdx.utils.Array;

public class CaseGroupData implements Cloneable {

    /* 说明表述 */
    public String desc;

    /* 间隔 unit:frame */
    public int interval;

    /* 间隔增量 */
    public float incrementValue;

    public Array<CaseData> cases = new Array<CaseData>();

    public Object cloneX() throws CloneNotSupportedException {
        CaseGroupData res = (CaseGroupData) super.clone();
        res.cases = new Array<CaseData>();
        for (int i = 0; i < cases.size; i++) {
            CaseData caseData = cases.get(i);
            res.cases.add((CaseData) caseData.cloneX());
        }
        return res;
    }
}
