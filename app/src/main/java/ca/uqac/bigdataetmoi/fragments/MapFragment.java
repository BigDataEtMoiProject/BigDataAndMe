package ca.uqac.bigdataetmoi.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ca.uqac.bigdataetmoi.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.Coordinate;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.workers.LocationWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static ca.uqac.bigdataetmoi.utils.Constants.LOCATION_PERMISSION_RESULT_CODE;


public class MapFragment extends Fragment {

    MapView map = null;

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

        if (hasAlreadyAcceptedLocationPermission()) {
            Context ctx = getContext();
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

            if (getView() != null) {
                map = getView().findViewById(R.id.map);
                if (map != null) {
                    map.setTileSource(TileSourceFactory.MAPNIK);

                    IMapController mapController = map.getController();
                    mapController.setZoom(14.5);
                    LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    GeoPoint startPoint = new GeoPoint(latitude, longitude);
                    mapController.setCenter(startPoint);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_RESULT_CODE) {
            if (hasGrantedLocationPermission(grantResults)) {
                displayMap();
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
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
            ((MainActivity) getActivity()).refreshViewPager();
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

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            updateMapPins(user);
        } else {
            Timber.e(response.toString());
        }
    }

    private void updateMapPins(User user) {
        if (user != null && getView() != null && user.coordinatesList.size() > 0 && map != null && getContext() != null) {

            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

            for (int i = 0; i < user.coordinatesList.size(); i++) {
                Coordinate coordinate = user.coordinatesList.get(i);
                GeoPoint point = new GeoPoint(Double.parseDouble(coordinate.latitude), Double.parseDouble(coordinate.longitude));
                items.add(new OverlayItem(coordinate.date, "", point));

                if (i == user.coordinatesList.size() - 1) {
                    map.getController().setCenter(point);
                }
            }

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            return true;
                        }

                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return false;
                        }
                    }, getContext());
            mOverlay.setFocusItemsOnTap(true);

            map.getOverlays().add(mOverlay);
        }

    }
}
