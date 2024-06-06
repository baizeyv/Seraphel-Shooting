package com.seraphel.shooting.constant;

public class Config {

    public static final int WIDTH = 1920;

    public static final int HEIGHT = 1080;

    /* ------------------------------------------------------------------------- */

    public static String device_name = "undefined";
    public static int availableMem = 256;
    public static int APILevel = 9;
    public static float scale = 0.8F;
    public static DeviceState device_state = DeviceState.good;
    public static int bannerHeight = 190;

    public enum DeviceState {
        poor, good
    }

    public static void init_device() {
        scale = 0.8F;
        device_state = DeviceState.good;
        if (isPoor()) {
            device_state = DeviceState.poor;
        }
        if (APILevel < 19 && (HEIGHT * WIDTH > 810 * 480 || availableMem < 256)) {
            device_state = DeviceState.poor;
            scale = 1;
        }
        if (device_name.equals("GT-P5110")
            || device_name.equals("GT-S5360")
            || device_name.equals("LG-H410")) {
            device_state = DeviceState.poor;
            scale = 0.5F;
        }
    }

    public static boolean isPoor() {
        return device_name.equals("DROIDX")
            || device_name.equals("DROID X2")
            || device_name.equals("SCH-I679")
            || device_state.equals("LG-H410");
    }

    /* ------------------------------------------------------------------------- */

}
