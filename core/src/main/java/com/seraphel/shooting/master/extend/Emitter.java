package com.seraphel.shooting.master.extend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;
import com.seraphel.shooting.SeraphelGame;
import com.seraphel.shooting.base.BaseScreen;
import com.seraphel.shooting.constant.Log;
import com.seraphel.shooting.master.Constant;
import com.seraphel.shooting.master.actor.TestBulletActor;
import com.seraphel.shooting.master.builtin.*;
import com.seraphel.shooting.master.builtin.data.ExecutionData;
import com.seraphel.shooting.master.builtin.data.PipeData;
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

    /* -------------------- split line -------------------- */

    public final Executable shootActuator;

    public final Executable conditionActuator;

    public final Executable resultActuator;

    /* -------------------- split line -------------------- */

    private final ArrayMap<ExecutionData, CaseData> executionMap = new ArrayMap<>();

    /* -------------------- split line -------------------- */

    /* 发射器所在管道的数据 */
    private PipeData pipeData;

    /* 发射器管道所在的节点数据 */
    private NodeTree nodeTree;

    /* 弹幕信息实例 */
    private Barrage barrage;

    /* -------------------- split line -------------------- */
    /* 公用虚拟方法 */
    private VirtualMethod method;

    /**
     * 条件MAP
     */
    private final ArrayMap<PropertyType, Float> conditionMap = new ArrayMap<>();

    /**
     * 结果MAP
     */
    private final ArrayMap<PropertyType, Float> resultMap = new ArrayMap<>();

    /* -------------------- split line -------------------- */

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
        /* ---------------------------------------------- */
        resultActuator = new Executable() {
            @Override
            public void execute(Event event) {
                try {
                    executionResult(event);
                } catch (Exception e) {
                    throw new GdxRuntimeException(e);
                }
            }
        };
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        // TODO: motion
    }

    /**
     * 初始设置 Ref 数据 TODO: 每次重置都需要调用
     */
    private void setupRef() {
        // 当前发射器所在的节点
        Node node = getNode();
        if (node == null)
            return;

        /* -------------------------------------------------- */
        // TODO: 恢复 ref 原始数据
        ref.id = data.id;
        ref.layerId = data.layerId;
        ref.detectionUnit = data.detectionUnit;
        ref.bindToId = data.bindToId;
        ref.deepBind = data.deepBind;
        ref.relativeDirection = data.relativeDirection;
        ref.x = data.x;
        ref.y = data.y;
        ref.startTime = data.startTime;
        ref.endTime = data.endTime;
        ref.shootX = data.shootX;
        ref.rdShootX = data.rdShootX;
        ref.shootY = data.shootY;
        ref.rdShootY = data.rdShootY;
        ref.radius = data.radius;
        ref.rdRadius = data.rdRadius;
        ref.radiusDegree = data.radiusDegree;
        ref.rdRadiusDegree = data.rdRadiusDegree;
        ref.count = data.count;
        ref.rdCount = data.rdCount;
        ref.cycle = data.cycle;
        ref.rdCycle = data.rdCycle;
        ref.angle = data.angle;
        ref.rdAngle = data.rdAngle;
        ref.range = data.range;
        ref.rdRange = data.rdRange;
        ref.speed = data.speed;
        ref.rdSpeed = data.rdSpeed;
        ref.speedDirection = data.speedDirection;
        ref.rdSpeedDirection = data.rdSpeedDirection;
        ref.acceleration = data.acceleration;
        ref.rdAcceleration = data.rdAcceleration;
        ref.accelerationDirection = data.accelerationDirection;
        ref.rdAccelerationDirection = data.rdAccelerationDirection;
        ref.bulletData = (BulletData) data.bulletData.cloneX();
        ref.removeOutOfScreen = data.removeOutOfScreen;
        ref.disappearEffect = data.disappearEffect;
        ref.tailEffect = data.tailEffect;
        ref.specialAngle = data.specialAngle;
        ref.caseGroups = new Array<>(data.caseGroups);
        /* -------------------------------------------------- */

        // 发射器的随机速度值
        float rdSpeed = MathUtils.lerp(-ref.rdSpeed, ref.rdSpeed, Constant.RANDOM.nextFloat()); // TODO: the third argument error
        // 发射器的随机速度方向值
        float rdSpeedDirection = MathUtils.lerp(-ref.rdSpeedDirection, ref.rdSpeedDirection, Constant.RANDOM.nextFloat());
        // 发射器的随机加速度值
        float rdAcceleration = MathUtils.lerp(-ref.rdAcceleration, ref.rdAcceleration, Constant.RANDOM.nextFloat());
        // 发射器的随机加速度方向值
        float rdAccelerationDirection = MathUtils.lerp(-ref.rdAccelerationDirection, ref.rdAccelerationDirection, Constant.RANDOM.nextFloat());

        if (data.shootX == Constant.SPECIAL_SELF) { // SHOOT_X 自身
            ref.shootX = node.getWorldX();
        } else if (data.shootX == Constant.SPECIAL_OTHER) { // SHOOT_X 自机
            ref.shootX = method.getOtherPosition().x;
        } else {
            ref.shootX = node.getWorldX() + data.shootX;
        }
        /* -------------------- split line -------------------- */
        if (data.shootY == Constant.SPECIAL_SELF) { // SHOOT_Y 自身
            ref.shootY = node.getWorldY();
        } else if (data.shootY == Constant.SPECIAL_OTHER) { // SHOOT_Y 自机
            ref.shootY = method.getOtherPosition().y;
        } else {
            ref.shootY = node.getWorldY() + data.shootY;
        }
        /* -------------------- split line -------------------- */
        if (data.speedDirection == Constant.SPECIAL_OTHER) { // 发射器的速度方向为自机
            ref.speedDirection = method.getOtherRotation(ref.shootX, ref.shootY);
        }
        if (data.accelerationDirection == Constant.SPECIAL_OTHER) { // 发射器的加速度方向为自机
            ref.accelerationDirection = method.getOtherRotation(ref.shootX, ref.shootY);
        }
        // 给发射器的 速度、速度方向、加速度、加速度方向 添加随机值
        ref.acceleration += rdAcceleration;
        ref.accelerationDirection += rdAccelerationDirection;
        ref.speed += rdSpeed;
        ref.speedDirection += rdSpeedDirection;
        /* -------------------- split line -------------------- */
        // 存储特殊的发射角度值
        ref.specialAngle = data.angle;
        // 存储特殊的子弹加速度方向值
        ref.bulletData.specialAccelerationDirection = data.bulletData.specialAccelerationDirection;
        /* -------------------- split line -------------------- */
        // 处理特殊的发射角度
        if (data.angle == Constant.SPECIAL_SELF) { // 发射器的发射角度为自身
            // TODO:发射x,y到node位置的角度 (ref.angle)
        } else if (data.angle == Constant.SPECIAL_OTHER) { // 发射器的发射角度为自机
            ref.angle = method.getOtherRotation(ref.shootX, ref.shootY);
        }
        /* -------------------- split line -------------------- */
        // 处理特殊的子弹加速度方向
        if (data.bulletData.accelerationDirection == Constant.SPECIAL_SELF) { // 子弹的加速度方向为自身
            // TODO:发射x,y到node位置的角度 (ref.bulletData.accelerationDirection)
        } else if (data.bulletData.accelerationDirection == Constant.SPECIAL_OTHER) { // 子弹的加速度方向为自机
            ref.bulletData.accelerationDirection = method.getOtherRotation(ref.shootX, ref.shootY);
        }
    }

    /**
     * 在构造函数之后需要调用,初始化可以调用的 data
     */
    @Override
    public void setBasicData(PipeData pipeData) {
        this.pipeData = pipeData;
    }

    @Override
    public void setupEntity(NodeTree nodeTree, VirtualMethod method) {
        this.nodeTree = nodeTree;
        this.method = method;
        setupRef();
    }

    @Override
    public void reset() {
        setupRef();
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

        // 条数随机增量
        float countRd = MathUtils.lerp(-ref.rdCount, ref.rdCount, Constant.RANDOM.nextFloat());
        // 发射x坐标随机增量
        float shootXRd = MathUtils.lerp(-ref.rdShootX, ref.rdShootX, Constant.RANDOM.nextFloat());
        // 发射y坐标随机增量
        float shootYRd = MathUtils.lerp(-ref.rdShootY, ref.rdShootY, Constant.RANDOM.nextFloat());
        // 发射半径随机增量
        float radiusRd = MathUtils.lerp(-ref.rdRadius, ref.rdRadius, Constant.RANDOM.nextFloat());
        // 发射半径方向随机增量
        float radiusDirectionRd = MathUtils.lerp(-ref.rdRadiusDegree, ref.rdRadiusDegree, Constant.RANDOM.nextFloat());
        // 发射范围随机增量
        float rangeRd = MathUtils.lerp(-ref.rdRange, ref.rdRange, Constant.RANDOM.nextFloat());
        // 发射角度随机增量
        float angleRd = MathUtils.lerp(-ref.rdAngle, ref.rdAngle, Constant.RANDOM.nextFloat());
        // 发射的子弹朝向随机增量
        float towardRd = MathUtils.lerp(-ref.bulletData.rdToward, ref.bulletData.rdToward, Constant.RANDOM.nextFloat());
        // 发射的子弹速度随机增量
        float bulletSpeedRd = MathUtils.lerp(-ref.bulletData.rdSpeed, ref.bulletData.rdSpeed, Constant.RANDOM.nextFloat());
        // 发射的子弹加速度随机增量
        float bulletAccelerationRd = MathUtils.lerp(-ref.bulletData.rdAcceleration, ref.bulletData.rdAcceleration, Constant.RANDOM.nextFloat());
        // 发射的子弹的加速度方向随机增量
        float bulletAccelerationDirectionRd = MathUtils.lerp(-ref.bulletData.rdAccelerationDirection, ref.bulletData.rdAccelerationDirection, Constant.RANDOM.nextFloat());

        for (int i = 0; i < ref.count + countRd; i++) {
            if (data.radiusDegree == Constant.SPECIAL_OTHER) { // 半径方向为自机
                // NOTE: 此时所有的半径方向变化事件均会失效, 事件会执行,但不会改变弹幕的表现
//                ref.radiusDegree = nodeTree.virtualMethod.getOtherRotation(, ); // TODO:
            }

            float partRotation = (ref.range + rangeRd) / (ref.count + countRd);
            float offsetRotation = (i - ((ref.count + countRd) - 1) / 2f) * partRotation; // 计算范围和条数后的角度

            // 只通过条数和范围来计算出的角度
            float noAngleDegree = worldRotation + offsetRotation;

            // TODO: 发射x,y坐标目前有错误
            float baseX = node.getWorldX();
            float baseY = node.getWorldY();
            baseX += (ref.radius + radiusRd) * MathUtils.cosDeg(noAngleDegree + ref.radiusDegree + radiusDirectionRd);
            baseY += (ref.radius + radiusRd) * MathUtils.sinDeg(noAngleDegree + ref.radiusDegree + radiusDirectionRd);

            // 最终发射方向 (角度值)
            float resultShootDirection = worldRotation + (ref.angle + angleRd) + offsetRotation;

            // NOTE: 在以下的两种情况下所有的发射角度变化事件均会失效, 事件会执行,但不会改变弹幕的表现
            if (ref.specialAngle == Constant.SPECIAL_SELF) { // 发射角度为自身
                // TODO:
            } else if (ref.specialAngle == Constant.SPECIAL_OTHER) { // 发射角度为自机
                resultShootDirection = nodeTree.virtualMethod.getOtherRotation(baseX, baseY) + angleRd + offsetRotation + worldRotation;
            }

            BulletData bulletData = (BulletData) ref.bulletData.cloneX();
            bulletData.speedDirection = resultShootDirection;
            bulletData.toward += towardRd;
            bulletData.speed += bulletSpeedRd;
            bulletData.acceleration += bulletAccelerationRd;

            // NOTE: 在以下的两种情况下所有的子弹加速度方向变化事件均会失效, 事件会执行,但不会改变弹幕的表现
            if (ref.bulletData.specialAccelerationDirection == Constant.SPECIAL_SELF) { // 子弹的加速度方向为自身
                // TODO:
            } else if (ref.bulletData.specialAccelerationDirection == Constant.SPECIAL_OTHER) { // 子弹的加速度方向为自机
                bulletData.accelerationDirection = nodeTree.virtualMethod.getOtherRotation(baseX, baseY) + bulletAccelerationDirectionRd;
            }

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
     * 恢复发射弹幕
     */
    public void recover() {
        // TODO:
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
            // TODO: 事件间隔及间隔增量的实现 (需要在CaseData 的 Detect 方法中有部分操作)
            if (cgData.interval <= 0)
                cgData.interval = 1;
            if ((event.time / Constant.getStandardFrameTime(ref.detectionUnit)) % cgData.interval == 0) {
                cgData.loopTimes ++;
            }
            for (CaseData caseData : cgData.cases) {
                if (caseData.detect(conditionMap, cgData, this)) {
                    // 通过条件检验了
                    if (caseData.specialRestore) {
                        recover();
                    } else if (caseData.specialShoot) {
                        try {
                            shoot();
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        if (caseData.loopTimes > 0) {
                            --caseData.loopTimes;
                            if (caseData.loopTimes == 0) {
                                caseData.loopTimes = -1;
                            }
                        }
                        ExecutionData eData = new ExecutionData();
                        eData.elapsedFrame = 0;
                        eData.targetValue = Float.parseFloat(caseData.resultValue);
                        eData.startValue = resultMap.get(caseData.propertyResult);
                        eData.finished = false;
                        executionMap.put(eData, caseData);
                    }
                }
            }
        }
    }

    public void executionResult(Event event) {
        for (ObjectMap.Entry<ExecutionData, CaseData> entry : executionMap) {
            ExecutionData item = entry.key;
            CaseData val = entry.value;
            if (item == null || item.finished || val == null)
                continue;
            // 特殊的效果部分
            if (val.propertyResult == PropertyType.TAIL_EFFECT) {
                ref.tailEffect = item.targetValue > 0;
                return;
            } else if (val.propertyResult == PropertyType.DISAPPEAR_EFFECT) {
                ref.disappearEffect = item.targetValue > 0;
                return;
            } else if (val.propertyResult == PropertyType.DISAPPEAR_OUT_SCREEN) {
                ref.removeOutOfScreen = item.targetValue > 0;
                return;
            }

            switch (val.curve.type) {
                case 0: { // 固定变化
                    switch (val.propertyResult) {
                        case X_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case Y_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.radius = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.radius += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.radius -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS_DEGREE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.radiusDegree = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.radiusDegree += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.radiusDegree -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case COUNT: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.count = (int) item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.count += (int) item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.count -= (int) item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case CYCLE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case ANGLE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.angle = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.angle += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.angle -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case RANGE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.range = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.range += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.range -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_LIFE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.life = (int) item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.life += (int) item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.life -= (int) item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TYPE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.type = (int) item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.type += (int) item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.type -= (int) item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_X: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.scaleX = (int) item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.scaleX += (int) item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.scaleX -= (int) item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_Y: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.scaleY = (int) item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.scaleY += (int) item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.scaleY -= (int) item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_R: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.r = item.targetValue / 255f;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.r = MathUtils.clamp(ref.bulletData.color.r + item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.r = MathUtils.clamp(ref.bulletData.color.r - item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_G: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.g = item.targetValue / 255f;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.g = MathUtils.clamp(ref.bulletData.color.g + item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.g = MathUtils.clamp(ref.bulletData.color.g - item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_B: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.b = item.targetValue / 255f;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.b = MathUtils.clamp(ref.bulletData.color.b + item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.b = MathUtils.clamp(ref.bulletData.color.b - item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_ALPHA: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.a = item.targetValue / 255f;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.a = MathUtils.clamp(ref.bulletData.color.a + item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.a = MathUtils.clamp(ref.bulletData.color.a - item.targetValue / 255f, 0f, 1f);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TOWARD: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.toward = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.toward += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.toward -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.speed = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.speed += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.speed -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.speedDirection = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.speedDirection += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.speedDirection -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.acceleration = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.acceleration += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.acceleration -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.accelerationDirection = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.accelerationDirection += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.accelerationDirection -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case HORIZONTAL_RATIO: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.horizontalRatio = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.horizontalRatio += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.horizontalRatio -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                        case VERTICAL_RATIO: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.verticalRatio = item.targetValue;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.verticalRatio += item.targetValue;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.verticalRatio -= item.targetValue;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
                case 1: { // 正比变化
                    // TODO;
                    switch (val.propertyResult) {
                        case X_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case Y_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.radius += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.radius);
                                }
                                break;
                                case INCREMENT: {
                                    ref.radius += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.radius -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS_DEGREE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.radiusDegree += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.radiusDegree);
                                }
                                break;
                                case INCREMENT: {
                                    ref.radiusDegree += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.radiusDegree -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case COUNT: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.count += (int) (1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.count));
                                }
                                break;
                                case INCREMENT: {
                                    ref.count += (int) (item.targetValue / val.duration);
                                }
                                break;
                                case DECREMENT: {
                                    ref.count -= (int) (item.targetValue / val.duration);
                                }
                                break;
                            }
                        }
                        break;
                        case CYCLE: {
                            // TODO: change timeline
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.cycle += (int) (1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.cycle));
                                }
                                break;
                                case INCREMENT: {
                                    ref.cycle += (int) (item.targetValue / val.duration);
                                }
                                break;
                                case DECREMENT: {
                                    ref.cycle -= (int) (item.targetValue / val.duration);
                                }
                                break;
                            }
                        }
                        break;
                        case ANGLE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.angle += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.angle);
                                }
                                break;
                                case INCREMENT: {
                                    ref.angle += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.angle -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case RANGE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.range += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.range);
                                }
                                break;
                                case INCREMENT: {
                                    ref.range += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.range -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_LIFE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.life += (int) (1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.life));
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.life += (int) (item.targetValue / val.duration);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.life -= (int) (item.targetValue / val.duration);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TYPE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.type += (int) (1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.type));
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.type += (int) (item.targetValue / val.duration);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.type -= (int) (item.targetValue / val.duration);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_X: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.scaleX += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.scaleX);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.scaleX += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.scaleX -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_Y: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.scaleY += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.scaleY);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.scaleY += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.scaleY -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_R: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.r = MathUtils.clamp(ref.bulletData.color.r + 1f / (val.duration - item.elapsedFrame) * (item.targetValue / 255f - ref.bulletData.color.r), 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.r = MathUtils.clamp(ref.bulletData.color.r + (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.r = MathUtils.clamp(ref.bulletData.color.r - (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_G: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.g = MathUtils.clamp(ref.bulletData.color.g + 1f / (val.duration - item.elapsedFrame) * (item.targetValue / 255f - ref.bulletData.color.g), 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.g = MathUtils.clamp(ref.bulletData.color.g + (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.g = MathUtils.clamp(ref.bulletData.color.g - (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_B: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.b = MathUtils.clamp(ref.bulletData.color.b + 1f / (val.duration - item.elapsedFrame) * (item.targetValue / 255f - ref.bulletData.color.b), 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.b = MathUtils.clamp(ref.bulletData.color.b + (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.b = MathUtils.clamp(ref.bulletData.color.b - (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_ALPHA: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.a = MathUtils.clamp(ref.bulletData.color.a + 1f / (val.duration - item.elapsedFrame) * (item.targetValue / 255f - ref.bulletData.color.a), 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.a = MathUtils.clamp(ref.bulletData.color.a + (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.a = MathUtils.clamp(ref.bulletData.color.a - (item.targetValue / 255f) / val.duration, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TOWARD: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.toward += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.toward);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.toward += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.toward -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.speed += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.speed);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.speed += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.speed -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.speedDirection += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.speedDirection);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.speedDirection += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.speedDirection -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.acceleration += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.acceleration);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.acceleration += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.acceleration -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.accelerationDirection += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.accelerationDirection);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.accelerationDirection += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.accelerationDirection -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case HORIZONTAL_RATIO: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.horizontalRatio += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.horizontalRatio);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.horizontalRatio += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.horizontalRatio -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                        case VERTICAL_RATIO: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.verticalRatio += 1f / (val.duration - item.elapsedFrame) * (item.targetValue - ref.bulletData.verticalRatio);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.verticalRatio += item.targetValue / val.duration;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.verticalRatio -= item.targetValue / val.duration;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
                case 2: { // 正弦变化
                    float percent = MathUtils.sin(MathUtils.PI * 2 * MathUtils.clamp((float) item.elapsedFrame / val.duration, 0, 1));
                    // TODO:
                    switch (val.propertyResult) {
                        case X_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case Y_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.radius = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.radius = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.radius = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS_DEGREE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.radiusDegree = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.radiusDegree = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.radiusDegree = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case COUNT: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.count = (int) (item.startValue + (item.targetValue - item.startValue) * percent);
                                }
                                break;
                                case INCREMENT: {
                                    ref.count = (int) (item.startValue + item.targetValue * percent);
                                }
                                break;
                                case DECREMENT: {
                                    ref.count = (int) (item.startValue - item.targetValue * percent);
                                }
                                break;
                            }
                        }
                        break;
                        case CYCLE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case ANGLE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.angle = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.angle = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.angle = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case RANGE: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.range = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.range = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.range = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.speed = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.speed = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.speed = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.speedDirection = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.speedDirection = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.speedDirection = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.acceleration = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.acceleration = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.acceleration = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.accelerationDirection = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.accelerationDirection = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.accelerationDirection = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_LIFE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.life = (int) (item.startValue + (item.targetValue - item.startValue) * percent);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.life = (int) (item.startValue + item.targetValue * percent);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.life = (int) (item.startValue - item.targetValue * percent);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TYPE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.type = (int) (item.startValue + (item.targetValue - item.startValue) * percent);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.type = (int) (item.startValue + item.targetValue * percent);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.type = (int) (item.startValue - item.targetValue * percent);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_X: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.scaleX = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.scaleX = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.scaleX = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_Y: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.scaleY = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.scaleY = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.scaleY = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_R: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.r = MathUtils.clamp(item.startValue + (item.targetValue / 255f - item.startValue) * percent, 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.r = MathUtils.clamp(item.startValue + item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.r = MathUtils.clamp(item.startValue - item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_G: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.g = MathUtils.clamp(item.startValue + (item.targetValue / 255f - item.startValue) * percent, 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.g = MathUtils.clamp(item.startValue + item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.g = MathUtils.clamp(item.startValue - item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_B: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.b = MathUtils.clamp(item.startValue + (item.targetValue / 255f - item.startValue) * percent, 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.b = MathUtils.clamp(item.startValue + item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.b = MathUtils.clamp(item.startValue - item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_ALPHA: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.color.a = MathUtils.clamp(item.startValue + (item.targetValue / 255f - item.startValue) * percent, 0, 1);
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.color.a = MathUtils.clamp(item.startValue + item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.color.a = MathUtils.clamp(item.startValue - item.targetValue / 255f * percent, 0, 1);
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TOWARD: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.toward = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.toward = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.toward = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.speed = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.speed = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.speed = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.speedDirection = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.speedDirection = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.speedDirection = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.acceleration = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.acceleration = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.acceleration = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION_DIRECTION: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.accelerationDirection = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.accelerationDirection = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.accelerationDirection = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case HORIZONTAL_RATIO: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.horizontalRatio = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.horizontalRatio = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.horizontalRatio = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                        case VERTICAL_RATIO: {
                            switch (val.changeType) {
                                case CHANGE_TO: {
                                    ref.bulletData.verticalRatio = item.startValue + (item.targetValue - item.startValue) * percent;
                                }
                                break;
                                case INCREMENT: {
                                    ref.bulletData.verticalRatio = item.startValue + item.targetValue * percent;
                                }
                                break;
                                case DECREMENT: {
                                    ref.bulletData.verticalRatio = item.startValue - item.targetValue * percent;
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
                case 3: { // 类spine基础曲线变化
                    // TODO:
                    switch (val.propertyResult) {
                        case X_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case Y_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS_DEGREE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case COUNT: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case CYCLE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case ANGLE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RANGE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_LIFE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TYPE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_X: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_Y: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_R: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_G: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_B: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_ALPHA: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TOWARD: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case HORIZONTAL_RATIO: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case VERTICAL_RATIO: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
                case 4: { // 类unity高级曲线变化
                    // TODO:
                    switch (val.propertyResult) {
                        case X_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case Y_AXIS_POSITION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RADIUS_DEGREE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case COUNT: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case CYCLE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case ANGLE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case RANGE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_SPEED_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case LAUNCHER_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_LIFE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TYPE: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_X: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SCALE_Y: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_R: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_G: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_B: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_COLOR_ALPHA: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_TOWARD: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_SPEED_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case BULLET_ACCELERATION_DIRECTION: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case HORIZONTAL_RATIO: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                        case VERTICAL_RATIO: {
                            // TODO:
                            switch (val.changeType) {
                                case CHANGE_TO: {

                                }
                                break;
                                case INCREMENT: {

                                }
                                break;
                                case DECREMENT: {

                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            item.elapsedFrame ++;
            if (val.curve.type == 2 && item.elapsedFrame == val.duration + 1) { // 正弦变化
                item.finished = true;
            } else {
                if (!(val.curve.type != 2 && item.elapsedFrame == val.duration))
                    return;
                item.finished = true;
            }
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
        if (nodeTree == null)
            return null;
        return nodeTree.findNode(pipeData.nodeData.name);
    }

    public void drawDebug(ShapeRenderer shapes) {
        Node node = getNode();
        float xx = node.getWorldX();
        float yy = node.getWorldY();
        shapes.setColor(Color.CYAN);
        shapes.circle(xx, yy, 10);
    }

    @Override
    public String toString() {
        return "Node: " + getNode().data.name + " Pipe: " + pipeData.name + " Emitter: " + name;
    }
}
