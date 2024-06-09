package com.seraphel.shooting.base;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.seraphel.shooting.constant.Asset;

public class BaseScene extends Group {

    public BaseScene(String layoutPartName) {
        addActor(Asset.fetch().loadLayout("layout/" + layoutPartName + ".json"));
    }

}
