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
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import ca.uqac.bigdataetmoi.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.workers.PhotoWorker;

import static ca.uqac.bigdataetmoi.utils.Constants.STORAGE_PERMISSION_RESULT_CODE;


public class GalleryFragment extends Fragment {

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (hasAlreadyAcceptedStoragePermission()) {
            startPhotoUploadingBackgroundWork();

            return inflater.inflate(R.layout.fragment_gallery, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_storage_permission, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addStoragePermissionButtonListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_RESULT_CODE) {
            if (hasGrantedStoragePermission(grantResults)) {
                refreshViewPager();
            }
        }
    }

    private void startPhotoUploadingBackgroundWork() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadLocationWorkRequest = new PeriodicWorkRequest
                .Builder(PhotoWorker.class, 2, TimeUnit.DAYS)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "PHOTO WORK",
                        ExistingPeriodicWorkPolicy.KEEP,
                        uploadLocationWorkRequest
                );
    }

    private void addStoragePermissionButtonListener() {
        Button locationPermissionButton = getView().findViewById(R.id.storage_continue);

        if (locationPermissionButton != null) {
            locationPermissionButton.setOnClickListener(v -> askForStoragePermission());
        }
    }

    private void askForStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_RESULT_CODE);
    }

    private boolean hasGrantedStoragePermission(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void refreshViewPager() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).refreshViewPager();
        }
    }

    private boolean hasAlreadyAcceptedStoragePermission() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }
}
