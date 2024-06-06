package com.seraphel.shooting.screen;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.seraphel.shooting.actor.SpriteActor;
import com.seraphel.shooting.base.BaseScreen;

public class MainScreen extends BaseScreen {


    public MainScreen() {

        SpriteActor spriteActor = new SpriteActor("test.plist", "border");
        addActor(spriteActor);

        spriteActor.debug();
        spriteActor.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(2 ,2, 2),
                Actions.scaleTo(0.5f, 0.5f, 2)
            )
        ));

    }
}
