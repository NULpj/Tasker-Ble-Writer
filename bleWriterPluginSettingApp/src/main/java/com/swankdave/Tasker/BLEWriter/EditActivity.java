package com.swankdave.Tasker.BLEWriter;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity;
import com.twofortyfouram.spackle.AppBuildInfo;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class EditActivity extends AbstractAppCompatPluginActivity {
    //private final static int REQUEST_ENABLE_BT = 1;
    Bundle CurrentBundle;
    private BluetoothAdapter bluetoothAdapter;
    private boolean isScanning = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ArrayAdapter<String> scanListAdapter;
    private AlertDialog scanDialog;
    private static final long SCAN_DURATION_MS = 8000;
    private static final int REQUEST_BT_SCAN = 1001;
    private static final int REQUEST_LOCATION = 1002;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                0);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                    0);
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;

        findViewById(R.id.BLE_Scan_Button).setOnClickListener(v -> startScan());
        ((Button) findViewById(R.id.BLE_Save_Button)).setOnClickListener(v -> {
            mIsCancelled = false;
            finish();
        });
        ((Button) findViewById(R.id.BLE_Cancel_Button)).setOnClickListener(v -> {
            mIsCancelled = true;
            finish();
        });

        // Load previous bundle from host if provided.
        Bundle previousBundle = getIntent().getBundleExtra("com.twofortyfouram.locale.intent.extra.BUNDLE");
        String previousBlurb = getIntent().getStringExtra("com.twofortyfouram.locale.intent.extra.BLURB");
        if (previousBundle != null) {
            onPostCreateWithPreviousResult(previousBundle, previousBlurb == null ? "" : previousBlurb);
        }
    }


    @Override
    public void onPostCreateWithPreviousResult(@NonNull final Bundle previousBnd, @NonNull final String previousBlurb) {
        BLEBundleManager previousBundle = new BLEBundleManager(previousBnd);
        ((EditText) findViewById(R.id.BLE_Device_Address)).setText(previousBundle.getDeviceAddress());
        ((EditText) findViewById(R.id.BLE_Service_Guid)).setText(previousBundle.getServiceGuid());
        ((EditText) findViewById(R.id.BLE_Characteristic_Guid)).setText(previousBundle.getCharacteristicGuid());
        ((EditText) findViewById(R.id.BLE_Value)).setText(previousBundle.getValue());
        ((CheckBox) findViewById(R.id.BLE_Send_As_Text)).setChecked(previousBundle.getSendAsText());
    }



    @Override
    public boolean isBundleValid(@NonNull final Bundle bundle) {
        return true;
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        final Bundle result = new Bundle();
        BLEBundleManager bundleInterface = new BLEBundleManager(result);

        bundleInterface.setDeviceAddress(((EditText) findViewById(R.id.BLE_Device_Address)).getText().toString());
        bundleInterface.setServiceGuid(((EditText) findViewById(R.id.BLE_Service_Guid)).getText().toString());
        bundleInterface.setCharacteristicGuid(((EditText) findViewById(R.id.BLE_Characteristic_Guid)).getText().toString());
        bundleInterface.setValue(((EditText) findViewById(R.id.BLE_Value)).getText().toString());
        bundleInterface.setSendAsText(((CheckBox) findViewById(R.id.BLE_Send_As_Text)).isChecked());
        result.putInt("com.swankdave.Tasker.BLEWriter.extra.INT_VERSION_CODE", AppBuildInfo.getVersionCode(getApplicationContext()));
        CurrentBundle = result;

        return result;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull final Bundle bundle) {
        BLEBundleManager BLEBundleManager = new BLEBundleManager(bundle);
        if (BLEBundleManager.getValue().isEmpty()) {
            return "Blank";
        }
        if (BLEBundleManager.getSendAsText())
            return BLEBundleManager.getValue() + " (text)";
        return BLEBundleManager.getValue();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        else if (R.id.menu_discard_changes == item.getItemId()) {
            // Signal to AbstractAppCompatPluginActivity that the user canceled.
            mIsCancelled = true;
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }

    private void startScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Toast.makeText(this, R.string.ble_error_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasScanPermission()) {
            requestScanPermission();
            return;
        }
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.ble_error_bluetooth_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isScanning) {
            return;
        }
        isScanning = true;

        scanListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        scanDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.ble_scan_title)
                .setAdapter(scanListAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String entry = scanListAdapter.getItem(which);
                        if (entry != null && entry.contains("(") && entry.contains(")")) {
                            String address = entry.substring(entry.lastIndexOf('(') + 1, entry.lastIndexOf(')'));
                            ((EditText) findViewById(R.id.BLE_Device_Address)).setText(address);
                        }
                        stopScan();
                    }
                })
                .setOnCancelListener(dialog -> stopScan())
                .create();
        scanDialog.show();

        bluetoothAdapter.startLeScan(leScanCallback);
        handler.postDelayed(this::stopScan, SCAN_DURATION_MS);
    }

    private void stopScan() {
        if (bluetoothAdapter != null && isScanning) {
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
        isScanning = false;
        if (scanDialog != null && scanDialog.isShowing()) {
            scanDialog.dismiss();
        }
    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(() -> addDeviceToList(device));
        }
    };

    private void addDeviceToList(BluetoothDevice device) {
        if (scanListAdapter == null || device == null || device.getAddress() == null) {
            return;
        }
        for (int i = 0; i < scanListAdapter.getCount(); i++) {
            if (scanListAdapter.getItem(i) != null && scanListAdapter.getItem(i).contains(device.getAddress())) {
                return;
            }
        }
        String label = (device.getName() == null || device.getName().isEmpty()) ?
                getString(R.string.ble_scan_unknown) : device.getName();
        scanListAdapter.add(label + " (" + device.getAddress() + ")");
        scanListAdapter.notifyDataSetChanged();
    }

    private boolean hasScanPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private void requestScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BT_SCAN);
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BT_SCAN) {
            if (hasScanPermission()) {
                startScan();
            } else {
                Toast.makeText(this, R.string.ble_error_permission_denied, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION) {
            if (hasLocationPermission()) {
                startScan();
            } else {
                Toast.makeText(this, R.string.ble_error_location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
