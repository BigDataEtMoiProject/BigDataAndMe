package ca.uqac.bigdataetmoi.permission_manager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import ca.uqac.bigdataetmoi.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECORD_AUDIO;

public class PermissionFragment extends Fragment implements IPermissionContract.View {

    private IPermissionContract.Presenter presenter;

    private Switch switchLocation, switchMicrophone,
            switchSMS, switchContacts, switchBluetooth, switchWifi, switchUsage;

    public PermissionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View rootView = inflater.inflate(R.layout.fragment_permission_manager, container, false);

        switchLocation = rootView.findViewById(R.id.switch_location);
        switchMicrophone = rootView.findViewById(R.id.switch_micro);
        switchSMS = rootView.findViewById(R.id.switch_sms);
        switchContacts = rootView.findViewById(R.id.switch_contacts);
        switchBluetooth = rootView.findViewById(R.id.switch_bluetooth);
        switchWifi = rootView.findViewById(R.id.switch_wifi);
        switchUsage = rootView.findViewById(R.id.switch_usage);

        return rootView;
    }

    @Override
    public void setListeners() {
        // Définition des événements des switch button
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(ACCESS_FINE_LOCATION, checked);
            }
        });

        switchMicrophone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(RECORD_AUDIO, checked);
            }
        });

        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(READ_SMS, checked);
            }
        });

        switchContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(READ_CONTACTS, checked);
            }
        });

        switchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(BLUETOOTH_ADMIN, checked);
            }
        });

        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(ACCESS_WIFI_STATE, checked);
            }
        });

        switchUsage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                presenter.setPermissionGranted(PACKAGE_USAGE_STATS, checked);
            }
        });
    }

    @Override
    public void setLocationChecked(boolean checked) {
        switchLocation.setChecked(checked);
    }

    @Override
    public void setMicrophoneChecked(boolean checked) {
        switchMicrophone.setChecked(checked);
    }

    @Override
    public void setSmsChecked(boolean checked) {
        switchSMS.setChecked(checked);
    }

    @Override
    public void setContactsChecked(boolean checked) {
        switchContacts.setChecked(checked);
    }

    @Override
    public void setBluetoothChecked(boolean checked) {
        switchBluetooth.setChecked(checked);
    }

    @Override
    public void setWifiChecked(boolean checked) {
        switchWifi.setChecked(checked);
    }

    @Override
    public void setUsageChecked(boolean checked) {
        switchUsage.setChecked(checked);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null)
            presenter.start();
    }

    @Override
    public void setPresenter(@NonNull IPermissionContract.Presenter presenter) {
        this.presenter = presenter;
    }

}
