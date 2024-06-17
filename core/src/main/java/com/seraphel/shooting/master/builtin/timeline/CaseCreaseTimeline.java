package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.CaseData;

public class CaseCreaseTimeline extends CurveTimeline {

    private final CaseData caseData;

    private final Emitter emitter;
    private final float totalTime;
    private final boolean positiveOrNegative;
    private float timeCounter;
    private float fixCounter;
    private boolean finished;

    public CaseCreaseTimeline(CaseData caseData, Emitter emitter, boolean positiveOrNegative) {
        super(2);
        this.caseData = caseData;
        this.emitter = emitter;
        this.positiveOrNegative = positiveOrNegative;
        timeCounter = 0;
        totalTime = caseData.duration * Constant.STANDARD_FRAME_TIME;
        finished = false;
        fixCounter = 0;
    }

    @Override
    public void call(float lastTime, float time) {
        if (finished)
            return;
        super.call(lastTime, time);
        if (caseData.curve == null)
            return;
        float deltaTime = Gdx.graphics.getDeltaTime();
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

                } else if (caseData.curve.type == 0) { // FIX 固定变化
                    // TODO: 固定增加、减少变化需要是每0.016665f秒进行一次添加, 这个时间需要根据弹幕的事件检测时长来获取
                    // TODO: 我觉得固定变化的增加减少用 EventTimeline 来实现更方便且更准确一些
                    timeCounter += deltaTime;
                    fixCounter += deltaTime;
                    if (timeCounter >= totalTime) {
                        finished = true;
                    }
                    float standard = Constant.getStandardFrameTime(emitter.ref.detectionUnit);
                    if (fixCounter >= standard) {
                        fixCounter -= standard;
                        if (positiveOrNegative) {
                            emitter.ref.angle += Float.parseFloat(caseData.resultValue);
                        } else {
                            emitter.ref.angle -= Float.parseFloat(caseData.resultValue);
                        }
                    }
                } else { // PRO AND CURVE
                    timeCounter += deltaTime;
                    if (timeCounter >= totalTime) {
                        deltaTime = timeCounter - totalTime;
                        finished = true;
                    }
                    float percent = MathUtils.clamp(deltaTime / totalTime, 0, 1);
                    percent = getCurvePercent(0, percent);
                    float changeValue = Float.parseFloat(caseData.resultValue) * percent;
                    if (positiveOrNegative)
                        emitter.ref.angle += changeValue;
                    else
                        emitter.ref.angle -= changeValue;
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
