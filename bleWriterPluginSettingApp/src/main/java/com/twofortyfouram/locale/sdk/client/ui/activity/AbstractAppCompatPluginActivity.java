package com.twofortyfouram.locale.sdk.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Simplified implementation of the Locale/Tasker plugin edit Activity base class.
 */
public abstract class AbstractAppCompatPluginActivity extends AppCompatActivity {

    protected static final String EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    protected static final String EXTRA_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";

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
            Intent resultIntent = new Intent();
            if (resultBundle != null) {
                resultIntent.putExtra(EXTRA_BUNDLE, resultBundle);
            }
            resultIntent.putExtra(EXTRA_BLURB, blurb);
            setResult(RESULT_OK, resultIntent);
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
