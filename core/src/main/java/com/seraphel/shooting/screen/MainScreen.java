package com.seraphel.shooting.screen;

import com.badlogic.gdx.utils.Align;
import com.seraphel.shooting.base.BaseScreen;
import com.seraphel.shooting.master.actor.SelfActor;
import com.seraphel.shooting.master.actor.TestActor;
import com.seraphel.shooting.scene.DebugScene;

public class MainScreen extends BaseScreen {

    private DebugScene debugScene;

    private SelfActor selfActor;

    public MainScreen() {
        debugScene = new DebugScene();
        addActor(debugScene);

        selfActor = new SelfActor();
        addActor(selfActor);
        selfActor.setOrigin(Align.center);
        selfActor.setPosition(300, 300, Align.center);
        selfActor.setScale(2f, 4f);

        TestActor testActor = new TestActor(selfActor);
        addActor(testActor);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        debugScene.resize(width, height);
    }
}
