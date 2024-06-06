package com.seraphel.shooting.constant;

import com.badlogic.gdx.assets.AssetManager;

public class Asset {

    private static Asset singleton;

    public AssetManager manager;

    private Asset() {
        manager = new AssetManager();
    }

    private void setupLoaders() {
        /* cocos layout JSON loader */
        // manager.setLoader(CCExport.class, new CocosUILoader(manager.getFileHandleResolver()));
    }
}
