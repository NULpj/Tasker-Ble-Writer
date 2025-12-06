package com.twofortyfouram.locale.sdk.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Minimal stub replacement for the original Locale/Tasker client receiver to allow building without
 * the external dependency.
 */
public abstract class AbstractPluginSettingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null || !isBundleValid(bundle)) {
            return;
        }
        firePluginSetting(context, bundle);
    }

    protected abstract boolean isBundleValid(@NonNull final Bundle bundle);

    protected abstract boolean isAsync();

    protected abstract void firePluginSetting(@NonNull final Context context, @NonNull final Bundle bundle);
}
