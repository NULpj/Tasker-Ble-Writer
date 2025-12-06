package com.twofortyfouram.locale.sdk.client.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Minimal stub replacement for the original Locale/Tasker client activity to allow building without
 * the external dependency.
 */
public abstract class AbstractAppCompatPluginActivity extends AppCompatActivity {

    // Flag used by the original library to communicate cancellation; kept for compatibility.
    protected boolean mIsCancelled = false;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void finish() {
        if (!mIsCancelled) {
            Bundle resultBundle = getResultBundle();
            String blurb = resultBundle != null ? getResultBlurb(resultBundle) : "";
            android.content.Intent result = new android.content.Intent();
            if (resultBundle != null) {
                result.putExtra("com.twofortyfouram.locale.intent.extra.BUNDLE", resultBundle);
            }
            result.putExtra("com.twofortyfouram.locale.intent.extra.BLURB", blurb);
            setResult(RESULT_OK, result);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }

    protected abstract void onPostCreateWithPreviousResult(@NonNull Bundle previousBundle, @NonNull String previousBlurb);

    public abstract boolean isBundleValid(@NonNull Bundle bundle);

    @Nullable
    public abstract Bundle getResultBundle();

    @NonNull
    public abstract String getResultBlurb(@NonNull Bundle bundle);
}
