package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.seraphel.shooting.SeraphelGame;
import com.seraphel.shooting.actor.SpriteActor;

public class SelfActor extends SpriteActor {

    private boolean isLeftKeyDown;

    private boolean isRightKeyDown;

    private boolean isUpKeyDown;

    private boolean isDownKeyDown;

    private boolean isShiftKeyDown;

    public SelfActor() {
        super("test.plist", "plate");
        InputAdapter inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.LEFT)
                    isLeftKeyDown = true;
                if (keycode == Input.Keys.RIGHT)
                    isRightKeyDown = true;
                if (keycode == Input.Keys.UP)
                    isUpKeyDown = true;
                if (keycode == Input.Keys.DOWN)
                    isDownKeyDown = true;
                if (keycode == Input.Keys.SHIFT_LEFT)
                    isShiftKeyDown = true;
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.LEFT)
                    isLeftKeyDown = false;
                if (keycode == Input.Keys.RIGHT)
                    isRightKeyDown = false;
                if (keycode == Input.Keys.UP)
                    isUpKeyDown = false;
                if (keycode == Input.Keys.DOWN)
                    isDownKeyDown = false;
                if (keycode == Input.Keys.SHIFT_LEFT)
                    isShiftKeyDown = false;
                return super.keyUp(keycode);
            }
        };
        SeraphelGame.ins.addInput(inputAdapter);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float speed = 100;
        if (isShiftKeyDown)
            speed *= 5;
        if (isLeftKeyDown)
            moveBy(-delta * speed, 0);
        if (isRightKeyDown)
            moveBy(delta * speed, 0);
        if (isUpKeyDown)
            moveBy(0, delta * speed);
        if (isDownKeyDown)
            moveBy(0, -delta * speed);
    }
}
