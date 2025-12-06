package com.twofortyfouram.spackle;

import android.content.Context;

/**
 * Minimal stub replacement for the original AppBuildInfo helper.
 */
public final class AppBuildInfo {

    private AppBuildInfo() {
    }

    public static int getVersionCode(Context context) {
        // Return a placeholder version code; adjust if real versioning is needed.
        return 1;
    }
}
