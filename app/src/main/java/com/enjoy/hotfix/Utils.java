package com.enjoy.hotfix;


public class Utils {

    private static final String TAG = "Utils";

    public static void test() {
//        Log.e(TAG, "修复...");
        throw new IllegalArgumentException("参数异常。。。。");
    }
}
