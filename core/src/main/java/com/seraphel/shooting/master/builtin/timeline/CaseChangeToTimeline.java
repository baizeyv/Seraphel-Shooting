package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.Gdx;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.CaseData;

public class CaseChangeToTimeline extends CurveTimeline {

    private final CaseData caseData;

    private final Emitter emitter;

    private boolean finished;

    public CaseChangeToTimeline(CaseData caseData, Emitter emitter) {
        super(1);
        this.caseData = caseData;
        this.emitter = emitter;
        finished = false;
    }

    @Override
    public void call(float lastTime, float time) {
        super.call(lastTime, time);
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (caseData.curve == null)
            return;
        if (caseData.curve.type == 2) { // SIN 正弦变化
            // TODO:
        } else if (caseData.curve.type == 0) { // FIX 固定变化
            finished = true;
            // TODO:
        } else {
            // TODO:
        }
        // TODO:
        switch (caseData.propertyResult) {
            case X_AXIS_POSITION: {
                // TODO:
            }
            break;
            case Y_AXIS_POSITION: {
                // TODO:
            }
            break;
            case RADIUS: {
                // TODO:
            }
            break;
            case RADIUS_DEGREE: {
                // TODO:
            }
            break;
            case COUNT: {
                // TODO:
            }
            break;
            case CYCLE: {
                // TODO:
            }
            break;
            case ANGLE: {
                // TODO:
            }
            break;
            case RANGE: {
                // TODO:
            }
            break;
            case LAUNCHER_SPEED: {
                // TODO:
            }
            break;
            case LAUNCHER_SPEED_DIRECTION: {
                // TODO:
            }
            break;
            case LAUNCHER_ACCELERATION: {
                // TODO:
            }
            break;
            case LAUNCHER_ACCELERATION_DIRECTION: {
                // TODO:
            }
            break;
            case BULLET_LIFE: {
                // TODO:
            }
            break;
            case BULLET_TYPE: {
                // TODO:
            }
            break;
            case BULLET_SCALE_X: {
                // TODO:
            }
            break;
            case BULLET_SCALE_Y: {
                // TODO:
            }
            break;
            case BULLET_COLOR_R: {
                // TODO:
            }
            break;
            case BULLET_COLOR_G: {
                // TODO:
            }
            break;
            case BULLET_COLOR_B: {
                // TODO:
            }
            break;
            case BULLET_COLOR_ALPHA: {
                // TODO:
            }
            break;
            case BULLET_TOWARD: {
                // TODO:
            }
            break;
            case BULLET_SPEED: {
                // TODO:
            }
            break;
            case BULLET_SPEED_DIRECTION: {
                // TODO:
            }
            break;
            case BULLET_ACCELERATION: {
                // TODO:
            }
            break;
            case BULLET_ACCELERATION_DIRECTION: {
                // TODO:
            }
            break;
            case HORIZONTAL_RATIO: {
                // TODO:
            }
            break;
            case VERTICAL_RATIO: {
                // TODO:
            }
            break;
        }
    }

}
