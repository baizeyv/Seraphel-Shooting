package com.seraphel.shooting.master.extend;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.seraphel.shooting.SeraphelGame;
import com.seraphel.shooting.base.BaseScreen;
import com.seraphel.shooting.constant.Log;
import com.seraphel.shooting.master.actor.TestBulletActor;
import com.seraphel.shooting.master.builtin.*;
import com.seraphel.shooting.master.builtin.data.PipeData;
import com.seraphel.shooting.master.builtin.timeline.CaseChangeToTimeline;
import com.seraphel.shooting.master.builtin.timeline.CaseCreaseTimeline;
import com.seraphel.shooting.master.builtin.timeline.CurveTimeline;
import com.seraphel.shooting.master.builtin.timeline.Timeline;
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

    private Barrage barrage;

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

    @Override
    public void bindBarrage(Barrage barrage) {
        this.barrage = barrage;
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

            // TODO:
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
        Node node = nodeTree.findNode(pipeData.nodeData.name);
        // 配置当前帧的条件MAP
        conditionMap.put(PropertyType.CURRENT_FRAME, event.time);
        // TODO:
        float baseX = node.getWorldX();
        float baseY = node.getWorldY();
        conditionMap.put(PropertyType.X_AXIS_POSITION, baseX);
        conditionMap.put(PropertyType.Y_AXIS_POSITION, baseY);
        conditionMap.put(PropertyType.RADIUS, ref.radius);
        conditionMap.put(PropertyType.RADIUS_DEGREE, ref.radiusDegree);
        conditionMap.put(PropertyType.COUNT, (float) ref.count);
        conditionMap.put(PropertyType.CYCLE, (float) ref.cycle);
        conditionMap.put(PropertyType.ANGLE, ref.angle);
        conditionMap.put(PropertyType.RANGE, ref.range);
        conditionMap.put(PropertyType.BULLET_SCALE_X, ref.bulletData.scaleX);
        conditionMap.put(PropertyType.BULLET_SCALE_Y, ref.bulletData.scaleY);
        conditionMap.put(PropertyType.BULLET_COLOR_ALPHA, ref.bulletData.color.a);
        conditionMap.put(PropertyType.BULLET_TOWARD, ref.bulletData.toward);

        // 配置当前帧的结果MAP
        resultMap.put(PropertyType.X_AXIS_POSITION, ref.shootX);
        resultMap.put(PropertyType.Y_AXIS_POSITION, ref.shootY);
        resultMap.put(PropertyType.RADIUS, ref.radius);
        resultMap.put(PropertyType.RADIUS_DEGREE, ref.radiusDegree);
        resultMap.put(PropertyType.COUNT, (float) ref.count);
        resultMap.put(PropertyType.CYCLE, (float) ref.cycle);
        resultMap.put(PropertyType.ANGLE, ref.angle);
        resultMap.put(PropertyType.RANGE, ref.range);
        resultMap.put(PropertyType.LAUNCHER_SPEED, ref.speed);
        resultMap.put(PropertyType.LAUNCHER_SPEED_DIRECTION, ref.speedDirection);
        resultMap.put(PropertyType.LAUNCHER_ACCELERATION, ref.acceleration);
        resultMap.put(PropertyType.LAUNCHER_ACCELERATION_DIRECTION, ref.accelerationDirection);
        resultMap.put(PropertyType.BULLET_LIFE, (float) ref.bulletData.life);
        resultMap.put(PropertyType.BULLET_TYPE, (float) ref.bulletData.type);
        resultMap.put(PropertyType.BULLET_SCALE_X, ref.bulletData.scaleX);
        resultMap.put(PropertyType.BULLET_SCALE_Y, ref.bulletData.scaleY);
        resultMap.put(PropertyType.BULLET_COLOR_R, ref.bulletData.color.r);
        resultMap.put(PropertyType.BULLET_COLOR_G, ref.bulletData.color.g);
        resultMap.put(PropertyType.BULLET_COLOR_B, ref.bulletData.color.b);
        resultMap.put(PropertyType.BULLET_COLOR_ALPHA, ref.bulletData.color.a);
        resultMap.put(PropertyType.BULLET_TOWARD, ref.bulletData.toward);
        resultMap.put(PropertyType.BULLET_SPEED, ref.bulletData.speed);
        resultMap.put(PropertyType.BULLET_SPEED_DIRECTION, ref.bulletData.speedDirection);
        resultMap.put(PropertyType.BULLET_ACCELERATION, ref.bulletData.acceleration);
        resultMap.put(PropertyType.BULLET_ACCELERATION_DIRECTION, ref.bulletData.accelerationDirection);
        resultMap.put(PropertyType.HORIZONTAL_RATIO, ref.bulletData.horizontalRatio);
        resultMap.put(PropertyType.VERTICAL_RATIO, ref.bulletData.verticalRatio);
        resultMap.put(PropertyType.DISAPPEAR_EFFECT, 0f);
        resultMap.put(PropertyType.DISAPPEAR_OUT_SCREEN, 0f);
        resultMap.put(PropertyType.TAIL_EFFECT, 0f);

        for (CaseGroupData cgData : ref.caseGroups) {
            // TODO: 事件间隔及间隔增量的实现
            for (CaseData caseData : cgData.cases) {
                if (caseData.detect(conditionMap, cgData, this)) {
                    // 通过条件检验了
                    switch (caseData.changeType) {
                        case INCREMENT: {
                            CurveTimeline timeline = new CaseCreaseTimeline(caseData, this, true);
                            curveTimelineSetting(timeline, caseData);
                            putMiddleTimeline(timeline);
                        }
                        break;
                        case DECREMENT: {
                            CurveTimeline timeline = new CaseCreaseTimeline(caseData, this, false);
                            curveTimelineSetting(timeline, caseData);
                            putMiddleTimeline(timeline);
                        }
                        break;
                        case CHANGE_TO: {
                            CurveTimeline timeline = new CaseChangeToTimeline(caseData, this);
                            curveTimelineSetting(timeline, caseData);
                            putMiddleTimeline(timeline);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void curveTimelineSetting(CurveTimeline timeline, CaseData caseData) {
        // TODO: timeline set
        switch (caseData.curve.type) {
            case 0: // FIX
                timeline.setFix(0);
                break;
            case 1: // PRO
                timeline.setPro(0);
                break;
            case 2: // SIN
                timeline.setSin(0);
                break;
            case 3: // basic curve
                break;
            case 4: // advance curve
                break;
        }
    }

    private void putMiddleTimeline(Timeline timeline) {
        if (barrage.timelines.containsKey(TimelinePriority.MIDDLE)) {
            barrage.timelines.get(TimelinePriority.MIDDLE).add(timeline);
        } else {
            Array<Timeline> arr = new Array<>();
            arr.add(timeline);
            barrage.timelines.put(TimelinePriority.MIDDLE, arr);
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
