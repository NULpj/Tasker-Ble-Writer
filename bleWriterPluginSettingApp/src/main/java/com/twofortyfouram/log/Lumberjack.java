package com.twofortyfouram.log;

import android.content.Context;
import android.util.Log;

/**
 * Lightweight logger replacement for the original Lumberjack utility.
 */
public final class Lumberjack {
    private static final String DEFAULT_TAG = "Lumberjack";

    private Lumberjack() {
    }

    public static void init(Context context) {
        // No-op for compatibility; could add structured logging here.
    }

    public static void i(String tag, String message) {
        Log.i(tag == null ? DEFAULT_TAG : tag, message);
    }

    public static void d(String tag, String message) {
        Log.d(tag == null ? DEFAULT_TAG : tag, message);
    }

    public static void w(String tag, String message) {
        Log.w(tag == null ? DEFAULT_TAG : tag, message);
    }

    public static void e(String tag, String message, Throwable tr) {
        Log.e(tag == null ? DEFAULT_TAG : tag, message, tr);
    }
}
