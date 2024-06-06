package com.seraphel.shooting.base;

import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class BaseViewport extends ExtendViewport {

    private float scaleX, scaleY, scale;

    private float overX, overY;

    private BaseStage stage;

    public BaseViewport(float minWorldWidth, float minWorldHeight) {
        super(minWorldWidth, minWorldHeight);
    }

    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        super.update(screenWidth, screenHeight, centerCamera);
        System.out.println("VIEWPORT -> " + getWorldWidth() + " " + getWorldHeight() + " " + getMinWorldWidth() + " " + getMinWorldHeight());
        scaleX = getWorldWidth() / getMinWorldWidth();
        scaleY = getWorldHeight() / getMinWorldHeight();
        scale = Math.max(scaleX, scaleY);
        float x = getMinWorldWidth() / 2 - getWorldWidth() / 2;
        float y = getMinWorldHeight() / 2 - getWorldHeight() / 2;
        overX = -x;
        overY = -y;

        if (stage != null)
            stage.Update(getWorldWidth(), getWorldHeight());
    }

    public void setStage(BaseStage stage) {
        this.stage = stage;
    }

    public float getScale() {
        return scale;
    }

    public float getOverX() {
        return overX;
    }

    public float getOverY() {
        return overY;
    }
}
