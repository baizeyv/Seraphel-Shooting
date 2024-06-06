package com.seraphel.shooting;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;
import com.seraphel.shooting.screen.MainScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SeraphelGame extends Game {
    @Override
    public void create () {
        setScreen(new MainScreen());
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
