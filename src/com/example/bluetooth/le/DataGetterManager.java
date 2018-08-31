package com.example.bluetooth.le;

import data.NikeDataGetter;

/**
 * Created by kuasmis on 15/6/26.
 */
public class DataGetterManager {

    private static NikeDataGetter leftDataGetter;
    private static NikeDataGetter rightDataGetter;

    public static void setLeftDataGetter(NikeDataGetter dataGetter) {
        leftDataGetter = dataGetter;
    }

    public static NikeDataGetter getLeftDataGetter() {
        return leftDataGetter;
    }

    public static void setRightDataGetter(NikeDataGetter dataGetter) {
        rightDataGetter = dataGetter;
    }

    public static NikeDataGetter getRightDataGetter() {
        return rightDataGetter;
    }
}
