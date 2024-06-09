package com.seraphel.shooting.master.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.seraphel.shooting.master.builtin.Launcher;
import com.seraphel.shooting.master.builtin.LauncherCollector;
import com.seraphel.shooting.master.builtin.enumerate.LauncherType;
import com.seraphel.shooting.master.builtin.data.EventData;
import com.seraphel.shooting.master.builtin.data.NodeData;
import com.seraphel.shooting.master.builtin.data.NodeTreeData;
import com.seraphel.shooting.master.builtin.data.PipeData;
import com.seraphel.shooting.master.builtin.timeline.Timeline;
import com.seraphel.shooting.master.extend.Emitter;
import com.seraphel.shooting.master.extend.data.*;
import com.seraphel.shooting.master.extend.enumerate.ChangeType;
import com.seraphel.shooting.master.extend.enumerate.OperatorType;
import com.seraphel.shooting.master.extend.enumerate.PropertyType;

public class NodeTreeJson {

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
            data.componentName = pipeMap.getString("component", null);

            nodeTreeData.pipes.add(data);
        }

        // ComponentCollector
        for (JsonValue collectorMap = root.getChild("collectors"); collectorMap != null; collectorMap = collectorMap.next) {
            LauncherCollector collector = new LauncherCollector(collectorMap.getString("name"));
            for (JsonValue entry = collectorMap.getChild("nodes"); entry != null; entry = entry.next) {
                NodeData node = nodeTreeData.findNode(entry.asString());
                if (node == null)
                    throw new SerializationException("Node not found: " + entry);
                collector.nodes.add(node);
            }
            for (JsonValue pipeEntry = collectorMap.getChild("components"); pipeEntry != null; pipeEntry = pipeEntry.next) {
                PipeData pipe = nodeTreeData.findPipe(pipeEntry.name);
                if (pipe == null)
                    throw new SerializationException("Pipe not found: " + pipeEntry.name);
                for (JsonValue entry = pipeEntry.child; entry != null; entry = entry.next) {
                    try {
                        Launcher launcher = readComponent(entry, collector, entry.name);
                        if (launcher != null)
                            collector.setComponent(pipe.index, entry.name, launcher);
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
        for (JsonValue eventMap = root.getChild("events");eventMap != null; eventMap = eventMap.next) {
            EventData data = new EventData(eventMap.name);
            nodeTreeData.events.add(data);
        }

        // Barrages.
        for (JsonValue barrageMap = root.getChild("barrages"); barrageMap != null; barrageMap = barrageMap.next) {
            try {
                // TODO: readBarrage()
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

    private Launcher readComponent(JsonValue map, LauncherCollector collector, String name) {
        name = map.getString("name", name);

        String type = map.getString("type", LauncherType.emitter.name());

        switch (LauncherType.valueOf(type)) {
            case emitter:{
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

                BulletData bulletData = new BulletData();
                bulletData.life = map.getInt("life", 0);
                bulletData.type = map.getInt("type", 0);
                bulletData.scaleX = map.getFloat("scaleX", 0);
                bulletData.scaleY = map.getFloat("scaleY", 0);
                int R = map.getInt("R", 0);
                int G = map.getInt("G", 0);
                int B = map.getInt("B", 0);
                int A = map.getInt("A", 0);
                bulletData.color = new Color(R / 255f, G / 255f, B / 255f, A / 100f);
                bulletData.toward = map.getFloat("toward", 0);
                bulletData.rdToward = map.getFloat("rdToward", 0);
                bulletData.towardSameAsSpeedDirection = map.getBoolean("towardSameAsSpeedDirection", false);
                bulletData.speed = map.getFloat("speed", 0);
                bulletData.rdSpeed  = map.getFloat("rdSpeed", 0);
                bulletData.acceleration = map.getFloat("acceleration", 0);
                bulletData.rdAcceleration = map.getFloat("rdAcceleration", 0);
                bulletData.accelerationDirection = map.getFloat("accelerationDirection", 0);
                bulletData.rdAccelerationDirection = map.getFloat("rdAccelerationDirection", 0);
                bulletData.horizontalRatio = map.getFloat("horizontalRatio", 0);
                bulletData.verticalRatio = map.getFloat("verticalRatio", 0);

                for (JsonValue caseGroupMap = map.getChild("bulletCaseGroups"); caseGroupMap != null; caseGroupMap = caseGroupMap.next) {
                    CaseGroupData caseGroupData = readCaseGroup(caseGroupMap);
                    bulletData.caseGroups.add(caseGroupData);
                }

                data.bulletData = bulletData;

                return new Emitter(data, collector, name);
            }
            case transmitter:{

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

        data.curveStartX = curveMap.getFloat("startX", 0);
        data.curveStartY = curveMap.getFloat("startY", 0);
        data.curveEndX = curveMap.getFloat("endX", 1);
        data.curveEndY = curveMap.getFloat("endY", 1);

        return data;
    }

    private void readBarrage(JsonValue map, String name, NodeTreeData nodeTreeData) {
        Array<Timeline> timelines = new Array<Timeline>();
        float duration = 0;
    }

}
