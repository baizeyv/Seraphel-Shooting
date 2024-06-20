package com.seraphel.shooting;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.seraphel.shooting.screen.MainScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SeraphelGame extends Game {

    public static SeraphelGame ins;

    private InputMultiplexer inputMultiplexer;

    public SeraphelGame() {
        ins = this;
        inputMultiplexer = new InputMultiplexer();
    }

    @Override
    public void create () {
        setScreen(new MainScreen());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void addInput(InputProcessor... input) {
        for (InputProcessor i : input) {
            inputMultiplexer.addProcessor(i);
        }
    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0, 1);
        super.render();
    }

    @Override
    public void dispose () {
    }

}
