package com.seraphel.shooting.screen;

import com.seraphel.shooting.base.BaseScreen;
import com.seraphel.shooting.master.actor.TestActor;
import com.seraphel.shooting.scene.DebugScene;

public class MainScreen extends BaseScreen {

    private DebugScene debugScene;

    public MainScreen() {
        debugScene = new DebugScene();
        addActor(debugScene);

        TestActor testActor = new TestActor();
        addActor(testActor);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        debugScene.resize(width, height);
    }
}
