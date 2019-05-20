package ca.uqac.bigdataetmoi.startup;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.maps.model.Marker;
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

        // Demandes des permissions
        requestPermissions(new String[] { Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_SMS }, 2);


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

                    if (presenter != null) presenter.start();
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
        Marker last = null;
        for(LocationData loc : locations) {
            //TODO:Markers aren't displaying :(
            last = googleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())));
        }
        if(last != null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(last.getPosition(), 10));
    }

    @Override
    public void afficherApplication() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("####################################################### PERMISSION_GRANTED pour READ_CONTACTS");
            } else if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) == false) { // permissions[0]
                displayOptions();
            } else {
                //expliquer pourquoi nous avons besoin de la permission
                System.out.println("####################################################### PERMISSION_DENIED pour READ_CONTACTS");
                explain();
            }
        }
    }

    // TODO mettre les textes dans le fichier strings
    private void displayOptions() {
        Snackbar.make(getView(), getString(R.string.permission_desactivee), Snackbar.LENGTH_LONG).setAction(getString(R.string.parametres), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

    // TODO mettre les textes dans le fichier strings
    private void explain() {
        Snackbar.make(getView(), getString(R.string.permission_message_information), Snackbar.LENGTH_LONG).setAction(getString(R.string.activer), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[] { Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_SMS }, 2);
            }
        }).show();
    }
}
