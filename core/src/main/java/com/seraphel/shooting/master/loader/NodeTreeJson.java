package com.seraphel.shooting.master.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.*;
import com.seraphel.shooting.master.builtin.Barrage;
import com.seraphel.shooting.master.builtin.Launcher;
import com.seraphel.shooting.master.builtin.LauncherCollector;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.data.NodeData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;
import com.seraphel.shooting.master.builtin.data.PipeData;
import com.seraphel.shooting.master.builtin.enumerate.LauncherType;
import com.seraphel.shooting.master.builtin.timeline.Timeline;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.*;
import com.seraphel.shooting.master.extend.enumerate.ChangeType;
import com.seraphel.shooting.master.extend.enumerate.OperatorType;
import com.seraphel.shooting.master.extend.enumerate.PropertyType;

public class NodeTreeJson {

    /* key -> collector name | value -> <launcher name, timeline array> */
    private final ArrayMap<String, ArrayMap<String, Array<Timeline>>> launcherTimelines = new ArrayMap<String, ArrayMap<String, Array<Timeline>>>();

    private void putLauncherTimeline(String collectorName, String launcherName, Array<Timeline> timelines) {
        if (launcherTimelines.containsKey(collectorName)) {
            ArrayMap<String, Array<Timeline>> map = launcherTimelines.get(collectorName);
            map.put(launcherName, timelines);
        } else {
            ArrayMap<String, Array<Timeline>> map = new ArrayMap<String, Array<Timeline>>();
            map.put(launcherName, timelines);
            launcherTimelines.put(collectorName, map);
        }
    }

    private Array<Timeline> getLauncherTimeline(String collectorName, String launcherName) {
        if (launcherTimelines.containsKey(collectorName)) {
            ArrayMap<String, Array<Timeline>> map = launcherTimelines.get(collectorName);
            if (map.containsKey(launcherName)) {
                return map.get(launcherName);
            }
        }
        return null;
    }

    protected JsonValue parse(FileHandle file) {
        if (file == null)
            throw new IllegalArgumentException("file is null");
        return new JsonReader().parse(file);
    }

    public NodeTreeData readNodeTreeData(FileHandle file) {
        if (file == null)
            throw new IllegalArgumentException("file is null");

        NodeTreeData nodeTreeData = new NodeTreeData();
        nodeTreeData.name = file.nameWithoutExtension();

        JsonValue root = parse(file);

        // NodeTree
        JsonValue nodeTreeMap = root.get("nodeTree");
        if (nodeTreeMap != null) {
            nodeTreeData.version = nodeTreeMap.getString("version", null);
        }

        // Nodes
        for (JsonValue nodeMap = root.getChild("nodes"); nodeMap != null; nodeMap = nodeMap.next) {
            NodeData parent = null;
            String parentName = nodeMap.getString("parent", null);
            if (parentName != null) {
                parent = nodeTreeData.findNode(parentName);
                if (parent == null)
                    throw new SerializationException("Parent node not found: " + parentName);
            }
            NodeData data = new NodeData(nodeTreeData.nodes.size, nodeMap.getString("name"), parent);
            data.x = nodeMap.getFloat("x", 0);
            data.y = nodeMap.getFloat("y", 0);
            data.rotation = nodeMap.getFloat("rotation", 0);
            data.scaleX = nodeMap.getFloat("scaleX", 1);
            data.scaleY = nodeMap.getFloat("scaleY", 1);
            data.shearX = nodeMap.getFloat("shearX", 0);
            data.shearY = nodeMap.getFloat("shearY", 0);

            nodeTreeData.nodes.add(data);
        }

        // Pipes
        for (JsonValue pipeMap = root.getChild("pipes"); pipeMap != null; pipeMap = pipeMap.next) {
            String pipeName = pipeMap.getString("name");
            String nodeName = pipeMap.getString("node");
            NodeData nodeData = nodeTreeData.findNode(nodeName);
            if (nodeData == null)
                throw new SerializationException("Pipe node not found: " + nodeName);
            PipeData data = new PipeData(nodeTreeData.pipes.size, pipeName, nodeData);
            data.launcherName = pipeMap.getString("launcher", null);

            nodeTreeData.pipes.add(data);
        }

        // LauncherCollector
        for (JsonValue collectorMap = root.getChild("collectors"); collectorMap != null; collectorMap = collectorMap.next) {
            LauncherCollector collector = new LauncherCollector(collectorMap.getString("name"));
            for (JsonValue pipeEntry = collectorMap.getChild("launchers"); pipeEntry != null; pipeEntry = pipeEntry.next) {
                PipeData pipe = nodeTreeData.findPipe(pipeEntry.name);
                if (pipe == null)
                    throw new SerializationException("Pipe not found: " + pipeEntry.name);
                for (JsonValue entry = pipeEntry.child; entry != null; entry = entry.next) {
                    try {
                        Launcher launcher = readLauncher(entry, collector, entry.name);
                        if (launcher != null) {
                            launcher.setBasicData(pipe);
                            collector.setLauncher(pipe.index, entry.name, launcher);
                        }
                    } catch (Throwable ex) {
                        throw new SerializationException("Error reading component: " + entry.name + ", collector: " + collector, ex);
                    }
                }
            }
            nodeTreeData.collectors.add(collector);
            if (collector.name.equals("default"))
                nodeTreeData.defaultCollector = collector;
        }

        // Events
        for (JsonValue eventMap = root.getChild("events"); eventMap != null; eventMap = eventMap.next) {
            EventData data = new EventData(eventMap.name);
            nodeTreeData.events.add(data);
        }

        // Barrages.
        for (JsonValue barrageMap = root.getChild("barrages"); barrageMap != null; barrageMap = barrageMap.next) {
            try {
                // TODO: readBarrage()
                readBarrage(barrageMap, barrageMap.name, nodeTreeData);
            } catch (Throwable ex) {
                throw new SerializationException("Error reading barrage: " + barrageMap.name, ex);
            }
        }

        nodeTreeData.nodes.shrink();
        nodeTreeData.pipes.shrink();
        nodeTreeData.collectors.shrink();
        nodeTreeData.events.shrink();
        nodeTreeData.barrages.shrink();

        return nodeTreeData;
    }

    private Launcher readLauncher(JsonValue map, LauncherCollector collector, String name) {
        name = map.getString("name", name);

        String type = map.getString("type", LauncherType.emitter.name());

        switch (LauncherType.valueOf(type)) {
            case emitter: {
                EmitterData data = new EmitterData();
                data.id = map.getInt("id", 0);
                data.layerId = map.getInt("layerId", 0);
                data.bindToId = map.getInt("bindToId", -1);
                data.deepBind = map.getBoolean("deepBind", false);
                data.relativeDirection = map.getBoolean("relativeDirection", false);
                data.x = map.getFloat("x", 0);
                data.y = map.getFloat("y", 0);
                data.startTime = map.getInt("startTime", 0);
                data.endTime = map.getInt("endTime", 0);
                data.shootX = map.getFloat("shootX", 0);
                data.rdShootX = map.getFloat("rdShootX", 0);
                data.shootY = map.getFloat("shootY", 0);
                data.rdShootY = map.getFloat("rdShootY", 0);
                data.radius = map.getFloat("radius", 0);
                data.rdRadius = map.getFloat("rdRadius", 0);
                data.radiusDegree = map.getFloat("radiusDegree", 0);
                data.rdRadiusDegree = map.getFloat("rdRadiusDegree", 0);
                data.count = map.getInt("count", 0);
                data.rdCount = map.getInt("rdCount", 0);
                data.cycle = map.getInt("cycle", 0);
                data.rdCycle = map.getInt("rdCycle", 0);
                data.angle = map.getFloat("angle", 0);
                data.rdAngle = map.getFloat("rdAngle", 0);
                data.range = map.getFloat("range", 0);
                data.rdRange = map.getFloat("rdRange", 0);
                data.speed = map.getFloat("speed", 0);
                data.rdSpeed = map.getFloat("rdSpeed", 0);
                data.speedDirection = map.getFloat("speedDirection", 0);
                data.rdSpeedDirection = map.getFloat("rdSpeedDirection", 0);
                data.acceleration = map.getFloat("acceleration", 0);
                data.rdAcceleration = map.getFloat("rdAcceleration", 0);
                data.accelerationDirection = map.getFloat("accelerationDirection", 0);
                data.rdAccelerationDirection = map.getFloat("rdAccelerationDirection", 0);
                data.removeOutOfScreen = map.getBoolean("removeOutOfScreen", false);
                data.disappearEffect = map.getBoolean("disappearEffect", false);
                data.tailEffect = map.getBoolean("tailEffect", false);

                for (JsonValue caseGroupMap = map.getChild("emitterCaseGroups"); caseGroupMap != null; caseGroupMap = caseGroupMap.next) {
                    CaseGroupData caseGroupData = readCaseGroup(caseGroupMap);
                    data.caseGroups.add(caseGroupData);
                }

                JsonValue bulletMap = map.get("bullet");
                BulletData bulletData = new BulletData();
                if (bulletMap != null) {
                    bulletData.life = bulletMap.getInt("life", 0);
                    bulletData.type = bulletMap.getInt("type", 0);
                    bulletData.scaleX = bulletMap.getFloat("scaleX", 1);
                    bulletData.scaleY = bulletMap.getFloat("scaleY", 1);
                    int R = bulletMap.getInt("R", 0);
                    int G = bulletMap.getInt("G", 0);
                    int B = bulletMap.getInt("B", 0);
                    int A = bulletMap.getInt("A", 0);
                    bulletData.color = new Color(R / 255f, G / 255f, B / 255f, A / 100f);
                    bulletData.toward = bulletMap.getFloat("toward", 0);
                    bulletData.rdToward = bulletMap.getFloat("rdToward", 0);
                    bulletData.towardSameAsSpeedDirection = bulletMap.getBoolean("towardSameAsSpeedDirection", false);
                    bulletData.speed = bulletMap.getFloat("speed", 0);
                    bulletData.speedDirection = bulletMap.getFloat("speedDirection", 0);
                    bulletData.rdSpeed = bulletMap.getFloat("rdSpeed", 0);
                    bulletData.acceleration = bulletMap.getFloat("acceleration", 0);
                    bulletData.rdAcceleration = bulletMap.getFloat("rdAcceleration", 0);
                    bulletData.accelerationDirection = bulletMap.getFloat("accelerationDirection", 0);
                    bulletData.rdAccelerationDirection = bulletMap.getFloat("rdAccelerationDirection", 0);
                    bulletData.horizontalRatio = bulletMap.getFloat("horizontalRatio", 1);
                    bulletData.verticalRatio = bulletMap.getFloat("verticalRatio", 1);
                }

                for (JsonValue caseGroupMap = map.getChild("bulletCaseGroups"); caseGroupMap != null; caseGroupMap = caseGroupMap.next) {
                    CaseGroupData caseGroupData = readCaseGroup(caseGroupMap);
                    bulletData.caseGroups.add(caseGroupData);
                }

                data.bulletData = bulletData;

                try {
                    Emitter res = new Emitter(data, collector, name);
                    Array<Timeline> timelines = TimelineGenerator.emitter(res);
                    putLauncherTimeline(collector.name, name, timelines);
                    return res;
                } catch (CloneNotSupportedException e) {
                    throw new GdxRuntimeException(e);
                }
            }
            case transmitter: {

            }
        }
        return null;
    }

    private CaseGroupData readCaseGroup(JsonValue caseGroupMap) {
        CaseGroupData caseGroupData = new CaseGroupData();
        caseGroupData.desc = caseGroupMap.getString("desc", "no description");
        caseGroupData.interval = caseGroupMap.getInt("interval", 1);
        caseGroupData.incrementValue = caseGroupMap.getInt("incrementValue", 0);
        for (JsonValue caseDataMap = caseGroupMap.getChild("cases"); caseDataMap != null; caseDataMap = caseDataMap.next) {
            CaseData caseData = new CaseData();
            int condA = caseDataMap.getInt("conditionA", -1);
            if (condA != -1)
                caseData.conditionA = PropertyType.values()[condA];
            int opA = caseDataMap.getInt("operatorA", -1);
            if (opA != -1)
                caseData.operatorA = OperatorType.values()[opA];
            String va = caseDataMap.getString("valueA", null);
            if (va != null)
                caseData.valueA = va;

            int condB = caseDataMap.getInt("conditionB", -1);
            if (condB != -1)
                caseData.conditionB = PropertyType.values()[condB];
            int opB = caseDataMap.getInt("operatorB", -1);
            if (opB != -1)
                caseData.operatorB = OperatorType.values()[opB];
            String vb = caseDataMap.getString("valueB", null);
            if (va != null)
                caseData.valueB = vb;

            int lo = caseDataMap.getInt("linkOperator", -1);
            if (lo != -1)
                caseData.linkOperator = OperatorType.values()[lo];

            int pr = caseDataMap.getInt("propertyResult", -1);
            if (pr != -1)
                caseData.propertyResult = PropertyType.values()[pr];

            caseData.duration = caseDataMap.getInt("duration", 0);

            caseData.curve = readCurve(caseDataMap.getChild("curve"));

            int ct = caseDataMap.getInt("changeType", -1);
            if (ct != -1)
                caseData.changeType = ChangeType.values()[ct];

            caseData.loopTimes = caseDataMap.getInt("loopTimes", 0);
            caseData.specialShoot = caseDataMap.getBoolean("specialShoot", false);
            caseData.specialRestore = caseDataMap.getBoolean("specialRestore", false);
            caseGroupData.cases.add(caseData);
        }
        return caseGroupData;
    }

    private CurveData readCurve(JsonValue curveMap) {
        CurveData data = new CurveData();

        data.time = curveMap.getFloat("time", 0);
        data.curveStartX = curveMap.getFloat("startX", 0);
        data.curveStartY = curveMap.getFloat("startY", 0);
        data.curveEndX = curveMap.getFloat("endX", 1);
        data.curveEndY = curveMap.getFloat("endY", 1);

        return data;
    }

    private void readBarrage(JsonValue map, String name, NodeTreeData nodeTreeData) {
        /* <收集者名称, 所有时间轴> */
        ArrayMap<String, Array<Timeline>> timelineMap = new ArrayMap<String, Array<Timeline>>();
        float duration = 0;

        duration = map.getFloat("duration");

        // Valid Pipes
        for (JsonValue pipeMap = map.getChild("pipes"); pipeMap != null; pipeMap = pipeMap.next) {
            String pipeName = pipeMap.asString(); // 生效的管道名称
            PipeData pipeData = nodeTreeData.findPipe(pipeName);
            if (pipeData == null)
                throw new GdxRuntimeException("Config Error.");
            Array<String> collectorNames = nodeTreeData.getAllCollectorNames();
            for (String collectorName : collectorNames) {
                Array<Timeline> timelines = getLauncherTimeline(collectorName, pipeData.launcherName);
                if (timelines != null) {
                    if (timelineMap.containsKey(collectorName)) {
                        timelineMap.get(collectorName).addAll(timelines);
                    } else {
                        timelineMap.put(collectorName, timelines);
                    }
                }
            }
        }

        // Node
        for (JsonValue nodeMap = map.getChild("nodes"); nodeMap != null; nodeMap = nodeMap.next) {
            NodeData node = nodeTreeData.findNode(nodeMap.name);
            if (node == null)
                throw new SerializationException("Node " + nodeMap.name + " not found.");
            for (JsonValue lineMap = nodeMap.child; lineMap != null; lineMap = lineMap.next) {
                String timelineType = lineMap.name;
                if (timelineType.equals("rotate")) {
                    // TODO:
                } else if (timelineType.equals("translate") || timelineType.equals("scale") || timelineType.equals("shear")) {
                    // TODO:
                } else {
                    throw new GdxRuntimeException("Invalid timeline type: " + timelineType);
                }
            }
        }

        // Event
        JsonValue eventsMap = map.get("events");
        if (eventsMap != null) {
            Timeline timeline = TimelineGenerator.builtinEvent(eventsMap, nodeTreeData);
            Array<String> collectorNames = nodeTreeData.getAllCollectorNames();
            for (String collectorName : collectorNames) {
                if (timelineMap.containsKey(collectorName)) {
                    timelineMap.get(collectorName).add(timeline);
                } else {
                    Array<Timeline> arr = new Array<Timeline>();
                    arr.add(timeline);
                    timelineMap.put(collectorName, arr);
                }
            }
            duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
        }

        for (ObjectMap.Entry<String, Array<Timeline>> arr : timelineMap) {
            arr.value.shrink();
        }
        nodeTreeData.barrages.add(new Barrage(name, timelineMap, duration));
    }

}
