package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.CaseData;
import com.seraphel.shooting.master.extend.data.EmitterData;

public class CaseChangeToTimeline extends CurveTimeline {

    private final CaseData caseData;

    private final Emitter emitter;

    private boolean finished;

    // 激活时的数据
    private final EmitterData startData;

    // 剩余时间
    private float remainingTime;

    public CaseChangeToTimeline(CaseData caseData, Emitter emitter) {
        super(2);
        this.caseData = caseData;
        this.emitter = emitter;
        try {
            this.startData = (EmitterData) emitter.ref.cloneX();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        finished = false;
        remainingTime = caseData.duration * Constant.STANDARD_FRAME_TIME;
    }

    @Override
    public void call(float lastTime, float time) {
        if (finished)
            return;
        super.call(lastTime, time);
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (caseData.curve == null)
            return;
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
                if (caseData.curve.type == 2) { // SIN 正弦变化
                    if (remainingTime <= 0) {
                        finished = true;
                    }
                    float total = caseData.duration * Constant.STANDARD_FRAME_TIME;
                    float percent = MathUtils.clamp((total - remainingTime) / total, 0, 1);
                    percent = getCurvePercent(0, percent);
                    emitter.ref.angle = startData.angle + (Float.parseFloat(caseData.resultValue) - startData.angle) * percent;
                    remainingTime -= deltaTime;
                } else if (caseData.curve.type == 0) { // FIX 固定变化
                    emitter.ref.angle = Float.parseFloat(caseData.resultValue) * getCurvePercent(0, 1);
                    remainingTime -= deltaTime;
                    if (remainingTime <= 0)
                        finished = true;
                } else { // PRO AND CURVE
                    // NOTE: ANGLE CHANGE_TO PRO 变化
                    float percent = MathUtils.clamp(deltaTime / remainingTime, 0, 1);
                    percent = getCurvePercent(0, percent);
                    emitter.ref.angle = (Float.parseFloat(caseData.resultValue) - emitter.ref.angle) * percent + emitter.ref.angle;
                    remainingTime -= deltaTime;
                    if (remainingTime <= 0) {
                        finished = true;
                    }
                }
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
