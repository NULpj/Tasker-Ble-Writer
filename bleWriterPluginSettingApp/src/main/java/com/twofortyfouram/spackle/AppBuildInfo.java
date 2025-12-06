package com.twofortyfouram.spackle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Replacement for the original AppBuildInfo helper that returns the app versionCode.
 */
public final class AppBuildInfo {

    private AppBuildInfo() {
    }

    public static int getVersionCode(Context context) {
        if (context == null) {
            return 1;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) info.getLongVersionCode();
            }
            return info.versionCode;
        } catch (Exception e) {
            return 1;
        }
    }
}
