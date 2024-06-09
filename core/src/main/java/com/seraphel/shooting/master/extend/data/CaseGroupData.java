package com.seraphel.shooting.master.extend.data;

import com.badlogic.gdx.utils.Array;

public class CaseGroupData {

    /* 说明表述 */
    public String desc;

    /* 间隔 unit:frame */
    public int interval;

    /* 间隔增量 */
    public float incrementValue;

    public final Array<CaseData> cases = new Array<CaseData>();

}
