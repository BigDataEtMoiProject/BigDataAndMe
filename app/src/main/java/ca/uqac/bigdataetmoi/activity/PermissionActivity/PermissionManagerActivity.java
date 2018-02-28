package ca.uqac.bigdataetmoi.activity.PermissionActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

public class PermissionManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_manager);

        final Switch switchLocation = (Switch) findViewById(R.id.switch_location),
                switchMicrophone = (Switch) findViewById(R.id.switch_micro),
                switchSMS = (Switch) findViewById(R.id.switch_sms),
                switchContacts = (Switch) findViewById(R.id.switch_contacts),
                switchBluetooth = (Switch) findViewById(R.id.switch_bluetooth),
                switchWifi = (Switch) findViewById(R.id.switch_wifi);

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionManager.getInstance().setPermissionGranted("ACCESS_FINE_LOCATION", b);
            }
        });

        switchMicrophone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionManager.getInstance().setPermissionGranted("RECORD_AUDIO", b);
            }
        });

        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionManager.getInstance().setPermissionGranted("BROADCAST_SMS", b);
            }
        });

        switchContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionManager.getInstance().setPermissionGranted("READ_CONTACTS", b);
            }
        });

        switchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionManager.getInstance().setPermissionGranted("BLUETOOTH_ADMIN", b);
            }
        });

        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionManager.getInstance().setPermissionGranted("ACCESS_WIFI_STATE", b);
            }
        });
    }
}