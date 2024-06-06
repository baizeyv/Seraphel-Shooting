package com.seraphel.shooting.constant;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.seraphel.shooting.base.BaseViewport;

public class Global {

    private static Global singleton;

    public BaseViewport viewport;

    public PolygonSpriteBatch batch;

    private Global() {
        this.viewport = new BaseViewport(Config.WIDTH, Config.HEIGHT);
        this.batch = new PolygonSpriteBatch();
    }

    public static Global fetch() {
        if (singleton == null)
            singleton = new Global();
        return singleton;
    }

}
