package com.seraphel.shooting.master.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.seraphel.shooting.constant.Asset;
import com.seraphel.shooting.constant.Config;
import com.seraphel.shooting.constant.Log;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.builtin.BarrageState;
import com.seraphel.shooting.master.builtin.NodeTree;
import com.seraphel.shooting.master.builtin.VirtualMethod;
import com.seraphel.shooting.master.builtin.data.BarrageStateData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;

public class TestActor extends Actor implements VirtualMethod {

    private final NodeTree nodeTree;

    BarrageState state;

    private final Vector2 tmpOffset = new Vector2();

    private final Vector2 offsetPosition = new Vector2(Vector2.Zero);

    private final Vector3 tmpInfo = new Vector3(Vector3.Zero);

    private final SelfActor selfActor;

    public TestActor(SelfActor selfActor) {
        this.selfActor = selfActor;
        // TODO:
        NodeTreeData data = Asset.fetch().loadNodeTree("testShoot.json");
        nodeTree = new NodeTree(data, this);
        BarrageStateData barrageStateData = new BarrageStateData(data);
        state = new BarrageState(barrageStateData);

        state.play("main", true);

        debug();
        setPosition(100, 200);
    }

    @Override
    public void act(float delta) {
        update();
        state.update(delta);
        state.apply();
        super.act(delta);
        nodeTree.updateWorldTransform();
    }

    private void update() {
        tmpOffset.set(Vector2.Zero);
        localToStageCoordinates(tmpOffset);
        tmpOffset.add(Config.WIDTH / 2f, Config.HEIGHT / 2f);
        offsetPosition.set(tmpOffset);
        localToStageInfo(getRotation(), getScaleX(), getScaleY());
    }

    protected final void localToStageInfo(float rotation, float scaleX, float scaleY) {
        Group parent = getParent();

        while (parent != null) {
            rotation += parent.getRotation();
            scaleX *= parent.getScaleX();
            scaleY *= parent.getScaleY();
            parent = parent.getParent();
        }
        tmpInfo.set(scaleX, scaleY, rotation);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
        float w = 100, h = 30;
        shapes.setColor(Color.GREEN);
        shapes.ellipse(getX() - w / 2f, getY() - h / 2f, w, h);
        shapes.ellipse(getX() - h / 2f, getY() - w / 2f, h, w);
        nodeTree.drawDebug(shapes);
    }

    @Override
    public Vector2 getOtherPosition() {
        return new Vector2(selfActor.getX(Align.center), selfActor.getY(Align.center));
    }

    @Override
    public float getOtherRotation(float myX, float myY) {
        Vector2 v1 = getOtherPosition();
        Vector2 v2 = new Vector2(myX, myY);
        return v1.sub(v2).angleDeg();
    }

    @Override
    public Vector2 getOffsetPosition() {
        return offsetPosition;
    }

    @Override
    public float getOffsetRotation() {
        return tmpInfo.z;
    }

    @Override
    public Vector2 getOffsetScale() {
        return new Vector2(tmpInfo.x, tmpInfo.y);
    }
}
