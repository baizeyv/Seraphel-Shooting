package com.seraphel.shooting.constant;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import net.mwplay.cocostudio.ui.base.ManagerUIEditor;
import net.mwplay.cocostudio.ui.base.loader.CocosUILoader;
import net.mwplay.cocostudio.ui.base.loader.ManagerUILoader;
import net.mwplay.cocostudio.ui.base.plist.PlistAtlas;
import net.mwplay.cocostudio.ui.base.plist.PlistAtlasLoader;
import net.mwplay.cocostudio.ui.model.CCExport;

public class Asset {

    private static Asset singleton;

    public AssetManager manager;

    private Asset() {
        manager = new AssetManager();
        setupLoaders();
    }

    private void setupLoaders() {
        /* cocos layout JSON loader */
        manager.setLoader(CCExport.class, new CocosUILoader(manager.getFileHandleResolver()));
        /* cocos plist loader */
        manager.setLoader(PlistAtlas.class, new PlistAtlasLoader(manager.getFileHandleResolver()));
        /* cocos ui loader */
        manager.setLoader(ManagerUIEditor.class, new ManagerUILoader(manager.getFileHandleResolver()));

        /* texture atlas loader */
        manager.setLoader(TextureAtlas.class, new TextureAtlasLoader(manager.getFileHandleResolver()));
    }

    public static Asset fetch() {
        if (singleton == null)
            singleton = new Asset();
        return singleton;
    }

    /* ----------------------------------------------------------------------------------------------- */

    public BitmapFont loadFont(String fontPath) {
        if (!manager.isLoaded(fontPath)) {
            BitmapFontLoader.BitmapFontParameter parameter = new BitmapFontLoader.BitmapFontParameter();
            manager.load(fontPath, BitmapFont.class, parameter);
            manager.finishLoading();
        }
        BitmapFont font = manager.get(fontPath, BitmapFont.class);
        font.getData().markupEnabled = true;
        return font;
    }

    public Group loadLayout(String layoutPath) {
        if (!manager.isLoaded(layoutPath)) {
            ManagerUILoader.ManagerUIParameter parameter = new ManagerUILoader.ManagerUIParameter("layout/", manager);
            manager.load(layoutPath, ManagerUIEditor.class, parameter);
            manager.finishLoading();
        }
        ManagerUIEditor managerUIEditor = manager.get(layoutPath);
        return managerUIEditor.createGroup();
    }

    public PlistAtlas loadPlist(String plistPath) {
        if (!manager.isLoaded(plistPath)) {
            manager.load(plistPath, PlistAtlas.class);
            manager.finishLoading();
        }
        return manager.get(plistPath);
    }

    public TextureAtlas loadAtlas(String atlasPath) {
        if (!manager.isLoaded(atlasPath)) {
            manager.load(atlasPath, TextureAtlas.class);
            manager.finishLoading();
        }
        return manager.get(atlasPath);
    }

}
