package com.seraphel.shooting.base;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.seraphel.shooting.constant.Asset;
import com.seraphel.shooting.constant.Global;

public class BaseScene extends Group {

    protected final Group root;

    protected final Group top, bottom, center;

    protected final Image background;

    public BaseScene(String layoutPartName) {
        root = Asset.fetch().loadLayout("layout/" + layoutPartName + ".json");
        addActor(root);
        top = root.findActor("Top");
        bottom = root.findActor("Bottom");
        center = root.findActor("Center");
        background = root.findActor("Background");
        adjust();
        init();
    }

    public void init() {
    }

    public void setup() {

    }

    protected void adjust() {
        top.setX(0);
        top.setY(root.getHeight() + Global.fetch().viewport.getOverY(), Align.top);
        bottom.setX(0);
        bottom.setY(-Global.fetch().viewport.getOverY());
        background.setOrigin(Align.center);
        background.setScale(Global.fetch().viewport.getScale());
    }

    public void resize(int width, int height) {
        adjust();
    }

}
