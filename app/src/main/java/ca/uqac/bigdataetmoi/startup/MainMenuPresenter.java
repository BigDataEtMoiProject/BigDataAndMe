package ca.uqac.bigdataetmoi.startup;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class MainMenuPresenter implements IMainMenuContract.Presenter, DataReadyListener {

    private IMainMenuContract.View view;
    private List<LocationData> locations;
    private int completedLocations = 0;

    public MainMenuPresenter(@NonNull IMainMenuContract.View view) {
        if(view != null) {
            this.view = view;
            view.setPresenter(this);
        }
        locations = new ArrayList<LocationData>();
    }

    @Override
    public void ouvrirDetailsEndroit(String idLocation) {

    }

    @Override
    public void ouvrirDetailsApplication(String idApplication) {

    }

    @Override
    public void fetchEndroits() {
        /**
         * Need to add the number of location data in the DB
         */
        LocationData donnee = new LocationData(this);
        locations.add(donnee);
    }

    @Override
    public void fetchApplication() {

    }

    @Override
    public void start() {
        fetchEndroits();
    }

    @Override
    public void dataReady(Object o) {
        if(o.getClass() == Boolean.class)
        {
            Boolean response = (Boolean) o;
            if(response.booleanValue())
                completedLocations++;

            if(locations.size() == completedLocations)
            {
                Log.d("BDEM", "debug");
            }
        }
    }
}
