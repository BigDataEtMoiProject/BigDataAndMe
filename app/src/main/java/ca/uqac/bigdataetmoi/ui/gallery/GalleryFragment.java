package ca.uqac.bigdataetmoi.ui.gallery;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.events.OnPhotoUploadedEvent;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.ui.MainActivity;
import ca.uqac.bigdataetmoi.workers.PhotoWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static ca.uqac.bigdataetmoi.utils.Constants.STORAGE_PERMISSION_RESULT_CODE;


public class GalleryFragment extends Fragment {

    GalleryAdapter galleryAdapter = null;
    RecyclerView galleryRecycler = null;

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

        if (hasAlreadyAcceptedStoragePermission()) {
            galleryAdapter = new GalleryAdapter(getContext());
            galleryRecycler = view.findViewById(R.id.gallery_recycler);
            galleryRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            galleryRecycler.setAdapter(galleryAdapter);
            fetchCurrentUserInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_RESULT_CODE) {
            if (hasGrantedStoragePermission(grantResults)) {
                refreshViewPager();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    // Todo: refactor cause not optimal (called in majority of fragments)
    private void fetchCurrentUserInfo() {
        UserRepository.getUserFromApi(getActivity()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleUserResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
                Timber.e(t);
            }
        });
    }

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                updateGalleryView(user);
            }
        } else {
            Timber.e(response.toString());
        }
    }

    private void updateGalleryView(User user) {
        if (getView() != null) {
            TextView galleryCardTitle = getView().findViewById(R.id.gallery_card_title);
            TextView galleryCardDescription = getView().findViewById(R.id.gallery_card_description);
            String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

            if (user.photoList != null && user.photoList.size() > 0) {
                Collections.reverse(user.photoList);
                galleryAdapter.submitList(user.photoList);
                galleryCardTitle.setText(user.photoList.size() + " éléments récupérés");
                if (galleryRecycler != null) {
                    galleryRecycler.smoothScrollToPosition(0);
                }
            } else {
                galleryCardTitle.setText("Aucun élément récupéré");
            }

            galleryCardDescription.setText("Dernière mise à jour: " + currentTime);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPhotoUploaded(OnPhotoUploadedEvent event) {
        User user = event.getUser();
        updateGalleryView(user);
    }
}
