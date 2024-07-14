package com.seraphel.shooting.master.builtin.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.seraphel.shooting.master.builtin.Barrage;
import com.seraphel.shooting.master.builtin.LauncherCollector;

public class NodeTreeData {

    public String name;

    public final Array<NodeData> nodes = new Array<NodeData>();

    public final Array<PipeData> pipes = new Array<PipeData>();

    public final Array<Barrage> barrages = new Array<Barrage>();

    public final ArrayMap<String, LauncherCollector> collectors = new ArrayMap<>();

    public LauncherCollector defaultCollector;

    public final Array<EventData> events = new Array<EventData>();

    public String version;

    public Barrage findBarrage(String name) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        Array<Barrage> barrages = this.barrages;
        for (int i = 0, n = barrages.size; i < n; i++) {
            Barrage barrage = barrages.get(i);
            if (barrage.name.equals(name))
                return barrage;
        }
        return null;
    }

    public PipeData findPipe(String name) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        Array<PipeData> pipes = this.pipes;
        for (int i = 0, n = pipes.size; i < n; i++) {
            PipeData pipe = pipes.get(i);
            if (pipe.name.equals(name))
                return pipe;
        }
        return null;
    }

    public NodeData findNode(String nodeName) {
        if (nodeName == null)
            throw new IllegalArgumentException("name cannot be null");
        Array<NodeData> nodes = this.nodes;
        for (int i = 0, n = nodes.size; i < n; i++) {
            NodeData node = nodes.get(i);
            if (node.name.equals(nodeName))
                return node;
        }
        return null;
    }

    public EventData findEvent(String eventName) {
        if (eventName == null)
            throw new IllegalArgumentException("name cannot be null");
        for (EventData eventData : events) {
            if (eventData.name.equals(eventName))
                return eventData;
        }
        return null;
    }

    public Array<String> getAllCollectorNames() {
        Array<String> res = new Array<String>();
        for (ObjectMap.Entry<String, LauncherCollector> entry : collectors) {
            res.add(entry.key);
        }
        return res;
    }

}
