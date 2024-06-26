package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.math.MathUtils;

public class CurveTimeline extends BaseTimeline {

    public static final float PRO = 0, FIX = 1, SIN = 2, BASIC_BEZIER = 3, ADVANCE_BEZIER = 4;

    private static final int BEZIER_SIZE = 10 * 2 - 1;

    private final float[] curves; // type, x, y

    public CurveTimeline(int frameCount) {
        super(frameCount);
        curves = new float[(frameCount - 1) * BEZIER_SIZE];
    }

    @Override
    public int getFrameCount() {
        return curves.length / BEZIER_SIZE + 1;
    }

    public void setPro(int frameIndex) {
        curves[frameIndex * BEZIER_SIZE] = PRO;
    }

    public void setFix(int frameIndex) {
        curves[frameIndex * BEZIER_SIZE] = FIX;
    }

    public void setSin(int frameIndex) {
        curves[frameIndex * BEZIER_SIZE] = SIN;
    }

    /**
     * BASIC_CURVE 需要使用这个
     *
     * @param frameIndex
     * @param cx1
     * @param cy1
     * @param cx2
     * @param cy2
     */
    public void setBasicCurve(int frameIndex, float cx1, float cy1, float cx2, float cy2) {
        float tmpx = (-cx1 * 2 + cx2) * 0.03f, tmpy = (-cy1 * 2 + cy2) * 0.03f;
        float dddfx = ((cx1 - cx2) * 3 + 1) * 0.006f, dddfy = ((cy1 - cy2) * 3 + 1) * 0.006f;
        float ddfx = tmpx * 2 + dddfx, ddfy = tmpy * 2 + dddfy;
        float dfx = cx1 * 0.3f + tmpx + dddfx * 0.16666667f, dfy = cy1 * 0.3f + tmpy + dddfy * 0.16666667f;

        int i = frameIndex * BEZIER_SIZE;
        float[] curves = this.curves;
        curves[i++] = BASIC_BEZIER;

        float x = dfx, y = dfy;
        for (int n = i + BEZIER_SIZE - 1; i < n; i += 2) {
            curves[i] = x;
            curves[i + 1] = y;
            dfx += ddfx;
            dfy += ddfy;
            ddfx += dddfx;
            ddfy += dddfy;
            x += dfx;
            y += dfy;
        }
    }

    public void setAdvanceCurve(int frameIndex) {
        // TODO:
        curves[frameIndex * BEZIER_SIZE] = ADVANCE_BEZIER;
    }

    /**
     * Returns the interpolated percentage for the specified key frame and linear percentage.
     */
    public float getCurvePercent(int frameIndex, float percent) {
        percent = MathUtils.clamp(percent, 0, 1);
        float[] curves = this.curves;
        int i = frameIndex * BEZIER_SIZE;
        float type = curves[i];
        if (type == PRO) return percent;
        if (type == FIX) return 1;
        if (type == SIN) {
            return MathUtils.sin(MathUtils.PI * 2 * percent);
        }
        if (type == BASIC_BEZIER) {
            i++;
            float x = 0;
            for (int start = i, n = i + BEZIER_SIZE - 1; i < n; i += 2) {
                x = curves[i];
                if (x >= percent) {
                    if (i == start) return curves[i + 1] * percent / x; // First point is 0,0.
                    float prevX = curves[i - 2], prevY = curves[i - 1];
                    return prevY + (curves[i + 1] - prevY) * (percent - prevX) / (x - prevX);
                }
            }
            float y = curves[i - 1];
            return y + (1 - y) * (percent - x) / (1 - x); // Last point is 1,1.
        }
        if (type == ADVANCE_BEZIER) {
            // TODO:
            return percent;
        }
        return percent;
    }
}
