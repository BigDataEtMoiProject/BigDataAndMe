package ca.uqac.bigdataetmoi.startup;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.data.LocationData;

public class MainMenuFragment extends Fragment implements IMainMenuContract.View {

    private IMainMenuContract.Presenter presenter;
    private MapView map;
    FusedLocationProviderClient location;
    GoogleMap googleMap;
    boolean mapReady = false;

    public MainMenuFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        map = rootView.findViewById(R.id.mapView);
        map.onCreate(savedBundle);
        map.onResume();

        location = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch(SecurityException e) {
            Log.d("BDEM", e.getMessage());
        }

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap _googleMap) {
                googleMap = _googleMap;
                mapReady = true;
                //Activation de la fonctionalite de googleMap pour le bouton de zoom sur la position actuelle
                try {
                    googleMap.setMyLocationEnabled(true);

                    /*
                        Recherche de la derniere position connu de google

                        Dans quelques cas, la position sera null:
                        1. Le senseur de localisation est desactive
                        2. L'appareil a ete formate
                        3. Google play service a ete restarte
                     */
                    location.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null) {
                                        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                    }
                                }
                            });

                    presenter.start();
                } catch (SecurityException e) {
                    Log.d("BDEM", e.getMessage());
                }
            }

        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null && mapReady)
            presenter.start();
    }

    @Override
    public void setPresenter(@NonNull IMainMenuContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void ouvrirDetailsEndroit(String idLocation) {

    }

    @Override
    public void ouvrirDetailsApplication(String idApplication) {

    }

    @Override
    public void afficherEndroits(List<LocationData> locations) {
        for(LocationData loc : locations) {
            //TODO:Markers aren't displaying :(
            googleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
        }
    }

    @Override
    public void afficherApplication() {

    }
}
