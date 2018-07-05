package com.serenity.viewinject;

import android.util.Log;

/**
 * Created by serenitynanian on 2018/7/5.
 */

class LogUtils {

    private static final String TAG = "InjectUtils";
    public static boolean isDebug = false ;
    protected static void e(String description){
        if (isDebug) {
            Log.e(TAG, description);
        }
    }
}
