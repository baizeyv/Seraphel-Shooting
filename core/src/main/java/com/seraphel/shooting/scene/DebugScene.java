package com.seraphel.shooting.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.seraphel.shooting.base.BaseScene;
import com.seraphel.shooting.constant.Global;

public class DebugScene extends BaseScene {

    private final Label renderCallsLabel;

    private final Label fpsLabel;

    public DebugScene() {
        super("Debug");
        renderCallsLabel = root.findActor("RenderCalls");
        fpsLabel = root.findActor("Fps");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        renderCallsLabel.setText("Render Calls: " + Global.fetch().batch.renderCalls);
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }
}
