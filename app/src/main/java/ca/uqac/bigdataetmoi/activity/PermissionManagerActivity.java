package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.*;

public class PermissionManagerActivity extends BaseActivity {

    private PermissionManager permissionManager;
    private Switch switchLocation, switchMicrophone,
            switchSMS, switchContacts, switchBluetooth, switchWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_manager);

        permissionManager = PermissionManager.getInstance();

        switchLocation = findViewById(R.id.switch_location);
        switchMicrophone = findViewById(R.id.switch_micro);
        switchSMS = findViewById(R.id.switch_sms);
        switchContacts = findViewById(R.id.switch_contacts);
        switchBluetooth = findViewById(R.id.switch_bluetooth);
        switchWifi = findViewById(R.id.switch_wifi);

        // Charger les valeurs selon le permissionManager
        updateSwitchButtons();

        // Définition des événements des switch button
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                permissionManager.setPermissionGranted(ACCESS_FINE_LOCATION, b);
            }
        });

        switchMicrophone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                permissionManager.setPermissionGranted(RECORD_AUDIO, b);
            }
        });

        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                permissionManager.setPermissionGranted(READ_SMS, b);
            }
        });

        switchContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                permissionManager.setPermissionGranted(READ_CONTACTS, b);
            }
        });

        switchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                permissionManager.setPermissionGranted(BLUETOOTH_ADMIN, b);
            }
        });

        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                permissionManager.setPermissionGranted(ACCESS_WIFI_STATE, b);
            }
        });
    }

    private void updateSwitchButtons() {
        switchLocation.setChecked(permissionManager.isGranted(ACCESS_FINE_LOCATION));
        switchMicrophone.setChecked(permissionManager.isGranted(RECORD_AUDIO));
        switchSMS.setChecked(permissionManager.isGranted(READ_SMS));
        switchContacts.setChecked(permissionManager.isGranted(READ_CONTACTS));
        switchBluetooth.setChecked(permissionManager.isGranted(BLUETOOTH_ADMIN));
        switchWifi.setChecked(permissionManager.isGranted(ACCESS_WIFI_STATE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updateSwitchButtons();
    }
}