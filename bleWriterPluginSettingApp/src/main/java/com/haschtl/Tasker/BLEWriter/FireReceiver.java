package com.haschtl.Tasker.BLEWriter;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;
import org.json.JSONObject;
import android.os.Bundle;

public final class FireReceiver extends AbstractPluginSettingReceiver {

    @Override
    protected boolean isJsonValid(@NonNull final JSONObject json) {
        return json.has("BLE_Address") && json.has("BLE_Service_Guid")
                && json.has("BLE_Characteristic_Guid") && json.has("BLE_Value");
    }

    @Override
    protected boolean isAsync() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void firePluginSetting(@NonNull final Context context, @NonNull final JSONObject json) {
        try {
            BundleExecutor.Execute(context, bundleFromJson(json));
        } catch (Exception ex) {
            android.widget.Toast.makeText(context, context.getString(R.string.ble_error_execute_failed), android.widget.Toast.LENGTH_LONG).show();
            android.util.Log.e("BLEWriter", "Failed to execute BLE write", ex);
        }
    }

    private Bundle bundleFromJson(JSONObject jsonObject) {
        Bundle bundle = new Bundle();
        BLEBundleManager mgr = new BLEBundleManager(bundle);
        mgr.setDeviceAddress(jsonObject.optString("BLE_Address", ""));
        mgr.setServiceGuid(jsonObject.optString("BLE_Service_Guid", ""));
        mgr.setCharacteristicGuid(jsonObject.optString("BLE_Characteristic_Guid", ""));
        mgr.setValue(jsonObject.optString("BLE_Value", ""));
        mgr.setSendAsText(jsonObject.optBoolean("BLE_Send_As_Text", false));
        return bundle;
    }
}
