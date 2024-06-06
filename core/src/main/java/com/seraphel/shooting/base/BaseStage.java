package com.seraphel.shooting.base;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Note: (0, 0) 点在屏幕最中间
 */
public class BaseStage extends Stage {

    public BaseStage(BaseViewport baseViewport) {
        super(baseViewport);
    }

    public BaseStage(BaseViewport baseViewport, Batch batch) {
        super(baseViewport, batch);
        getRoot().setSize(baseViewport.getMinWorldWidth(), baseViewport.getMinWorldHeight());
        float worldWidth = getViewport().getWorldWidth();
        float worldHeight = getViewport().getWorldHeight();
        baseViewport.setStage(this);
        Update(worldWidth, worldHeight);
        getRoot().debug();
    }

    public void Update(float worldWidth, float worldHeight) {
        System.out.println("WorldWidth: " + worldWidth + " <--> " + "WorldHeight: " + worldHeight);
        Viewport vp = getViewport();
        vp.getCamera().position.set(0, 0, 0);
        float overY = 0, overX = 0;
        if (vp instanceof BaseViewport) {
            getRoot().setSize(((BaseViewport) vp).getMinWorldWidth(), ((BaseViewport) vp).getMinWorldHeight());
            overX = ((BaseViewport) vp).getOverX();
            overY = ((BaseViewport) vp).getOverY();
        } else {
            getRoot().setSize(vp.getWorldWidth(), vp.getWorldHeight());
        }
        getRoot().setPosition(0, 0, Align.center);
    }

}
