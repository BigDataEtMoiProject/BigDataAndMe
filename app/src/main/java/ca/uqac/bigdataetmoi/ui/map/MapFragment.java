package ca.uqac.bigdataetmoi.ui.map;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.uqac.bigdataetmoi.events.OnLocationUploadedEvent;
import ca.uqac.bigdataetmoi.ui.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.City;
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
    MapLocationAdapter mapLocationAdapter = null;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (hasAlreadyAcceptedLocationPermission()) {
            //startLocationBackgroundWork();

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
                    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        GeoPoint startPoint = new GeoPoint(latitude, longitude);
                        mapController.setCenter(startPoint);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Todo: refactor cause not optimal (called in majority of fragments)
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

        if (hasAlreadyAcceptedLocationPermission() && getView() != null) {
            CityClickListener onRecentMovesItemClick = (view, city) -> {
                if (map != null) {
                    GeoPoint point = new GeoPoint(Double.parseDouble(city.latitude), Double.parseDouble(city.longitude));
                    map.getController().animateTo(point);
                }
            };
            mapLocationAdapter = new MapLocationAdapter(onRecentMovesItemClick);
            RecyclerView coordinatesRecycler = getView().findViewById(R.id.recent_moves_recycler);
            coordinatesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            coordinatesRecycler.setAdapter(mapLocationAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_RESULT_CODE) {
            if (hasGrantedLocationPermission(grantResults)) {
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
            locationPermissionButton.setOnClickListener(v -> askForLocationPermission());
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

    private void refreshViewPager() {
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
                        "LOCATION WORK",
                        ExistingPeriodicWorkPolicy.KEEP,
                        uploadLocationWorkRequest
                );
    }

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                updateMapPins(user);
                updateRecentMoves(user);
                updateLastUpdateTime();
            }
        } else {
            Timber.e(response.toString());
        }
    }

    private void updateLastUpdateTime() {
        if (getView() != null) {
            TextView lastKnownTextView = getView().findViewById(R.id.lastKnownUpdate);
            if (lastKnownTextView != null) {
                String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
                lastKnownTextView.setText("Dernière mise à jour: " + currentTime);
            }
        }
    }

    private void updateRecentMoves(User user) {
        ArrayList<Coordinate> coordinates = removeUndefinedCityLocation(user);
        List<City> cityList = new ArrayList<>();

        if (getView() != null) {
            CardView recentMovesCardView = getView().findViewById(R.id.map_recent_moves_card);

            if (recentMovesCardView != null) {
                if (coordinates.size() == 0) {
                    recentMovesCardView.setVisibility(View.INVISIBLE);
                } else {
                    recentMovesCardView.setVisibility(View.VISIBLE);
                }
            }
        }

        for (Coordinate coordinate : coordinates) {
            if (!isCityAlreadyInArray(cityList, coordinate.city)) {
                int numberOfCoordinates = getNumberOfCoordinatesForGivenCity(coordinates, coordinate.city);
                City city = new City(coordinate.city, coordinate.longitude, coordinate.latitude, numberOfCoordinates);
                cityList.add(city);
            }
        }

        sortCityListByNumberOfCoordinates(cityList);

        if (mapLocationAdapter != null) {
            mapLocationAdapter.submitList(cityList);
        }
    }

    private List<City> sortCityListByNumberOfCoordinates(List<City> cityList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Comparator<City> comparator = Comparator.comparing(City::getNumberOfCoordinatesLogged);
            Collections.sort(cityList, comparator.reversed());
        }

        return cityList;
    }

    private Boolean isCityAlreadyInArray(List<City> cityList, String cityName) {
        for (City city : cityList) {
            if (city.name.equals(cityName)) return true;
        }

        return false;
    }

    private int getNumberOfCoordinatesForGivenCity(List<Coordinate> coordinates, String cityName) {
        int numberOfCoordinates = 0;

        for (Coordinate coordinate : coordinates) {
            if (coordinate.city.equals(cityName)) {
                numberOfCoordinates++;
            }
        }

        return numberOfCoordinates;
    }

    private ArrayList<Coordinate> removeUndefinedCityLocation(User user) {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

        for (int i = 0; i < user.coordinatesList.size(); i++) {
            Coordinate coordinate = user.coordinatesList.get(i);

            if (coordinate.city != null && !coordinate.city.equals("undefined")) {
                coordinates.add(coordinate);
            }
        }

        return coordinates;
    }

    // TODO: refactor
    private void updateMapPins(User user) {
        if (getView() != null && user.coordinatesList.size() > 0 && map != null && getContext() != null) {

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUploaded(OnLocationUploadedEvent event) {
        User user = event.getUser();
        updateMapPins(user);
        updateRecentMoves(user);
        updateLastUpdateTime();
    }
}
