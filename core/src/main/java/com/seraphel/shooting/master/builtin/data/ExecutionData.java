package com.seraphel.shooting.master.builtin.data;

/**
 * 执行器数据
 */
public class ExecutionData {

    /**
     * 已经经过的帧数 unit: frame
     */
    public int elapsedFrame;

    /**
     * 目标值
     */
    public float targetValue;

    /**
     * 起始值
     */
    public float startValue;

    /**
     * 完成标识符
     */
    public boolean finished;

}
