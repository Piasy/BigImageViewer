/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.biv.example;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.piasy.biv.utils.IOUtils;
import com.squareup.leakcanary.LeakCanary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * Created by Piasy{github.com/Piasy} on 22/04/2017.
 */

public class App extends Application {
    static final String TAG = "BIV-App";

    static void fixLeakCanary696(Context context) {
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

    static boolean isEmui() {
        return !TextUtils.isEmpty(getSystemProperty("ro.build.version.emui"));
    }

    static String getSystemProperty(String propName) {
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

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
