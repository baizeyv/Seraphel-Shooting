package com.seraphel.shooting.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.seraphel.shooting.constant.Asset;
import net.mwplay.cocostudio.ui.base.plist.PlistAtlas;

public class SpriteActor extends Actor {

    /* main sprite */
    private Sprite sprite;

    public SpriteActor(PlistAtlas plistAtlas, String regionName) {
        TextureAtlas.AtlasRegion region = plistAtlas.findRegion(regionName);
        if (region != null) {
            sprite = new TextureAtlas.AtlasSprite(region);
            sprite.setOrigin(0, 0);
            super.setSize(sprite.getWidth(), sprite.getHeight());
        }
    }

    public SpriteActor(TextureAtlas textureAtlas, String regionName) {
        TextureAtlas.AtlasRegion region = textureAtlas.findRegion(regionName);
        if (region != null) {
            sprite = new TextureAtlas.AtlasSprite(region);
            sprite.setOrigin(0, 0);
            super.setSize(sprite.getWidth(), sprite.getHeight());
        }
    }

    public SpriteActor(String atlasPath, String regionName) {
        if (atlasPath.isEmpty())
            return;
        String[] arr = atlasPath.split("/");
        String suffix = arr[arr.length - 1].split("\\.")[1];
        if (suffix.equals("atlas")) {
            TextureAtlas atlas = Asset.fetch().loadAtlas(atlasPath);
            TextureAtlas.AtlasRegion region = atlas.findRegion(regionName);
            if (region != null)
                sprite = new TextureAtlas.AtlasSprite(region);
        } else if (suffix.equals("plist")) {
            PlistAtlas plist = Asset.fetch().loadPlist(atlasPath);
            TextureAtlas.AtlasRegion region = plist.findRegion(regionName);
            if (region != null)
                sprite = new TextureAtlas.AtlasSprite(region);
        }
        if (sprite != null) {
            sprite.setOrigin(0, 0);
            super.setSize(sprite.getWidth(), sprite.getHeight());
        }
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        if (sprite != null)
            sprite.setPosition(getX(), getY());
    }

    @Override
    protected void rotationChanged() {
        super.rotationChanged();
        if (sprite != null)
            sprite.setRotation(getRotation());
    }

    @Override
    protected void scaleChanged() {
        super.scaleChanged();
        if (sprite != null)
            sprite.setScale(getScaleX(), getScaleY());
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (sprite != null) {
            sprite.setSize(getWidth(), getHeight());
        }
    }

    @Override
    public void setOrigin(int alignment) {
        super.setOrigin(alignment);
        if (sprite != null)
            sprite.setOrigin(getOriginX(), getOriginY());
    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        if (sprite != null)
            sprite.setOrigin(getOriginX(), getOriginY());
    }

    @Override
    public void setOriginX(float originX) {
        super.setOriginX(originX);
        if (sprite != null)
            sprite.setOrigin(getOriginX(), getOriginY());
    }

    @Override
    public void setOriginY(float originY) {
        super.setOriginY(originY);
        if (sprite != null)
            sprite.setOrigin(getOriginX(), getOriginY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (sprite != null)
            sprite.draw(batch, parentAlpha);
    }

}
