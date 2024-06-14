package com.seraphel.shooting.master.extend;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.seraphel.shooting.SeraphelGame;
import com.seraphel.shooting.base.BaseScreen;
import com.seraphel.shooting.constant.Log;
import com.seraphel.shooting.master.actor.TestBulletActor;
import com.seraphel.shooting.master.builtin.*;
import com.seraphel.shooting.master.builtin.data.PipeData;
import com.seraphel.shooting.master.extend.data.BulletData;
import com.seraphel.shooting.master.extend.data.CaseData;
import com.seraphel.shooting.master.extend.data.CaseGroupData;
import com.seraphel.shooting.master.extend.data.EmitterData;
import com.seraphel.shooting.master.extend.enumerate.PropertyType;

public class Emitter implements Launcher {

    /* 引用数据,可修改 */
    public final EmitterData ref;
    /* 源数据,不可修改 */
    private final EmitterData data;

    private final LauncherCollector collector;

    private final String name;

    public final Executable shootActuator;

    public final Executable conditionActuator;

    private PipeData pipeData;

    private NodeTree nodeTree;

    /**
     * 条件MAP
     */
    private final ArrayMap<PropertyType, Float> conditionMap = new ArrayMap<>();

    /**
     * 结果MAP
     */
    private final ArrayMap<PropertyType, Float> resultMap = new ArrayMap<>();

    public Emitter(EmitterData data, LauncherCollector collector, String name) throws CloneNotSupportedException {
        this.data = data;
        this.ref = (EmitterData) this.data.cloneX();
        this.collector = collector;
        this.name = name;
        /* ---------------------------------------------- */
        shootActuator = new Executable() {
            @Override
            public void execute(Event event) {
                try {
                    shoot();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        /* ---------------------------------------------- */
        conditionActuator = new Executable() {
            @Override
            public void execute(Event event) {
                try {
                    detectCondition(event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * 在构造函数之后需要调用,初始化可以调用的 data
     */
    @Override
    public void setBasicData(PipeData pipeData) {
        this.pipeData = pipeData;
    }

    @Override
    public void setupEntity(NodeTree nodeTree) {
        this.nodeTree = nodeTree;
    }

    /**
     * 发射弹幕
     */
    public void shoot() throws CloneNotSupportedException {
        Log.success("SHOOT !!!");
        Node node = nodeTree.findNode(pipeData.nodeData.name);
        float worldRotation = node.getWorldRotationX();

        for (int i = 0; i < ref.count; i++) {
            float partRotation = ref.range / ref.count;
            float offsetRotation = (i - (ref.count - 1) / 2f) * partRotation; // 计算范围和条数后的角度

            // 只通过条数和范围来计算出的角度
            float noAngleDegree = worldRotation + offsetRotation;

            float baseX = node.getWorldX();
            float baseY = node.getWorldY();
            baseX += ref.radius * MathUtils.cosDeg(noAngleDegree + ref.radiusDegree);
            baseY += ref.radius * MathUtils.sinDeg(noAngleDegree + ref.radiusDegree);

            // 最终发射方向 (角度值)
            float resultShootDirection = worldRotation + ref.angle + offsetRotation;

            BulletData bulletData = (BulletData) ref.bulletData.cloneX();
            bulletData.speedDirection = resultShootDirection;
            TestBulletActor testBulletActor = new TestBulletActor("test.plist", "border", bulletData, this);
            testBulletActor.setOrigin(Align.center);
            testBulletActor.setPosition(baseX, baseY, Align.center);
            testBulletActor.applyScale();
            testBulletActor.applyRotation();
            testBulletActor.applyColor();
            BaseScreen bs = (BaseScreen) SeraphelGame.ins.getScreen();
            bs.addActor(testBulletActor);
        }
    }

    /**
     * 检测条件
     */
    public void detectCondition(Event event) {
        for (CaseGroupData cgData : ref.caseGroups) {
            // TODO: 事件间隔及间隔增量的实现
            for (CaseData caseData : cgData.cases) {

            }
        }
    }

    public NodeTree getNodeTree() {
        return nodeTree;
    }

    public Node getNode() {
        return nodeTree.findNode(pipeData.nodeData.name);
    }

    public void drawDebug(ShapeRenderer shapes) {
        Node node = getNode();
        float xx = node.getWorldX();
        float yy = node.getWorldY();
        shapes.circle(xx, yy, 20);
    }
}
