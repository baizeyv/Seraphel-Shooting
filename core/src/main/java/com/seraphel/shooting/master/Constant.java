package com.seraphel.shooting.master;

import java.util.Random;

public class Constant {

    public static float STANDARD_FRAME_TIME = 0.03333f;

    /**
     * 自身
     */
    public static float SPECIAL_SELF = -99998F;

    /**
     * 自机
     */
    public static float SPECIAL_OTHER = -99999F;

    public static float STANDARD_FRAME_TIME_60 = 0.016665f;

    public static Random RANDOM = new Random();

    public static float getStandardFrameTime(int unit) {
        switch (unit) {
            case 60:
                return STANDARD_FRAME_TIME_60;
            default:
                return STANDARD_FRAME_TIME;
        }
    }

}
