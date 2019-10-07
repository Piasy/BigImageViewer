package com.github.piasy.biv.example;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.piasy.biv.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class Utils {

    public static final String TAG = "BIV-App";

    public static void fixLeakCanary696(Context context) {
        if (!isEmui()) {
            Log.w(TAG, "not emui");
            return;
        }
        try {
            Class clazz = Class.forName("android.gestureboost.GestureBoostManager");
            Log.w(TAG, "clazz " + clazz);

            Field _sGestureBoostManager = clazz.getDeclaredField("sGestureBoostManager");
            _sGestureBoostManager.setAccessible(true);
            Field _mContext = clazz.getDeclaredField("mContext");
            _mContext.setAccessible(true);

            Object sGestureBoostManager = _sGestureBoostManager.get(null);
            if (sGestureBoostManager != null) {
                _mContext.set(sGestureBoostManager, context);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private static boolean isEmui() {
        return !TextUtils.isEmpty(getSystemProperty("ro.build.version.emui"));
    }

    private static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            IOUtils.closeQuietly(input);
        }
        return line;
    }
}
