package com.seraphel.shooting.master.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;

public class NodeTreeDataLoader extends AsynchronousAssetLoader<NodeTreeData, NodeTreeDataLoader.Parameter> {

    NodeTreeDataLoaderInfo info = new NodeTreeDataLoaderInfo();

    public NodeTreeDataLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, Parameter parameter) {
        info.nodeTreeData = new NodeTreeData();
        info.type = "json"
        ;

        if (info.type.equals("json")) {
            NodeTreeJson nodeTreeJson = new NodeTreeJson();
            info.nodeTreeData = nodeTreeJson.readNodeTreeData(file);
        } else if (info.type.equals("cs")) {

        } else {
            throw new GdxRuntimeException("Seraphel Shooting ERROR !");
        }
    }

    @Override
    public NodeTreeData loadSync(AssetManager manager, String fileName, FileHandle file, Parameter parameter) {
        return info.nodeTreeData;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Parameter parameter) {
        return null;
    }

    public static class NodeTreeDataLoaderInfo {
        NodeTreeData nodeTreeData;
        String type;
    }

    public static class Parameter extends AssetLoaderParameters<NodeTreeData> {

    }

}
