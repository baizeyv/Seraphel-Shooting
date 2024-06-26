package com.seraphel.shooting.base;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.seraphel.shooting.SeraphelGame;
import com.seraphel.shooting.constant.Global;
import com.seraphel.shooting.constant.Log;

public class BaseScreen implements Screen {

    protected BaseStage stage;

    public BaseScreen() {
        this.stage = new BaseStage(Global.fetch().viewport, Global.fetch().batch);
        SeraphelGame.ins.addInput(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Log.info("Screen Size -> width: " + width + ", height:" + height);
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }

    public void addActor(Actor actor) {
        stage.addActor(actor);
    }

}
