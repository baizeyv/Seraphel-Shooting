package com.seraphel.shooting.master.builtin;

import com.seraphel.shooting.master.builtin.data.PipeData;

public interface Launcher {

    public void setBasicData(PipeData pipeData);

    public void setupEntity(NodeTree nodeTree, VirtualMethod method);

    public void reset();

    public void bindBarrage(Barrage barrage);

}
