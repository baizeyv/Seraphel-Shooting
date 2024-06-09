package com.seraphel.shooting.master;

public class Utils {

    /**
     * Binary Search
     * @param values values array
     * @param target after the first and before the last value
     * @param step step
     * @return index of first value greater than the target
     */
    public static int binarySearch(float[] values, float target, int step) {
        int low = 0;
        int high = values.length / step - 2;
        if (high == 0) return step;
        int current = high >>> 1;
        while (true) {
            if (values[(current + 1) * step] <= target)
                low = current + 1;
            else
                high = current;
            if (low == high) return (low + 1) * step;
            current = (low + high) >>> 1;
        }
    }

    /**
     * Binary Search Step 1
     * @param values values array
     * @param target after the first and before the last value
     * @return index of first value greater than the target
     */
    public static int binarySearch(float[] values, float target) {
        return binarySearch(values, target, 1);
    }

}
