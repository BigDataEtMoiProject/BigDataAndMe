package ca.uqac.bigdataetmoi.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import ca.uqac.bigdataetmoi.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.workers.LocationWorker;

import static ca.uqac.bigdataetmoi.utils.Constants.LOCATION_PERMISSION_RESULT_CODE;


public class MapFragment extends Fragment {


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (hasAlreadyAcceptedLocationPermission()) {
            startLocationBackgroundWork();

            return inflater.inflate(R.layout.fragment_map, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_location_permission, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addLocationPermissionButtonListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_RESULT_CODE) {
            if (hasGrantedLocationPermission(grantResults)) {
                displayMap();
            }
        }
    }

    private void addLocationPermissionButtonListener() {
        Button locationPermissionButton = getView().findViewById(R.id.location_continue);

        if (locationPermissionButton != null) {
            locationPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForLocationPermission();
                }
            });
        }
    }

    private void askForLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_RESULT_CODE);
    }

    private boolean hasAlreadyAcceptedLocationPermission() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }
    
    private boolean hasGrantedLocationPermission(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void displayMap() {
        if (getActivity() != null) {
            ((MainActivity)getActivity()).refreshViewPager();
        }
    }

    private void startLocationBackgroundWork() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadLocationWorkRequest = new PeriodicWorkRequest
                .Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "UPLOAD WORK",
                        ExistingPeriodicWorkPolicy.KEEP,
                        uploadLocationWorkRequest
                );

        
    }

}
