package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.Gdx;
import com.seraphel.shooting.master.extend.data.CaseData;

public class CaseChangeToTimeline extends CurveTimeline {

    private final CaseData caseData;

    public CaseChangeToTimeline(int frameCount, CaseData caseData) {
        super(frameCount);
        this.caseData = caseData;
    }

    @Override
    public void call(float lastTime, float time) {
        super.call(lastTime, time);
        float deltaTime = Gdx.graphics.getDeltaTime();
    }

}
