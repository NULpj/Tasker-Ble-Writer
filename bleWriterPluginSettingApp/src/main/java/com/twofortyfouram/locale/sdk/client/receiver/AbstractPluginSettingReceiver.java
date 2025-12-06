package com.twofortyfouram.locale.sdk.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

/**
 * Simplified implementation of the Locale/Tasker plugin receiver base class.
 */
public abstract class AbstractPluginSettingReceiver extends BroadcastReceiver {

    private static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
        if (bundle == null) {
            bundle = intent.getExtras();
        }
        if (bundle == null || !isBundleValid(bundle)) {
            return;
        }

        if (isAsync()) {
            final Bundle data = new Bundle(bundle);
            final Context appContext = context.getApplicationContext();
            final PendingResult result = goAsync();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                try {
                    firePluginSetting(appContext, data);
                } finally {
                    result.finish();
                }
            });
        } else {
            firePluginSetting(context, bundle);
        }
    }

    protected abstract boolean isBundleValid(@NonNull final Bundle bundle);

    protected abstract boolean isAsync();

    protected abstract void firePluginSetting(@NonNull final Context context, @NonNull final Bundle bundle);
}
