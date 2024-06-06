package net.mwplay.cocostudio.ui.base.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import net.mwplay.cocostudio.ui.model.*;

public class CocosUILoader extends AsynchronousAssetLoader<CCExport, CocosUILoader.CCExportParameter> {

    private CCExport ccExport;

    public CocosUILoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, CCExportParameter parameter) {
        String jsonStr = file.readString("utf-8");
        Json json = new Json();
        json.addClassTag(CCData.class.getName(),CCData.class);
        json.addClassTag(GameProjectData.class.getName(),GameProjectData.class);
        json.addClassTag(CColor.class.getName(),CColor.class);
        json.addClassTag(FileData.class.getName(),FileData.class);
        json.setIgnoreUnknownFields(true);
        ccExport = json.fromJson(CCExport.class, jsonStr);
    }

    @Override
    public CCExport loadSync(AssetManager manager, String fileName, FileHandle file, CCExportParameter parameter) {
        return ccExport;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, CCExportParameter parameter) {
        return null;
    }

    static public class CCExportParameter extends AssetLoaderParameters<CCExport> {

    }
}
