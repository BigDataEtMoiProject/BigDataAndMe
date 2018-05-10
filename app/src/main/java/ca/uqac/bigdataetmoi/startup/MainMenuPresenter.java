package ca.uqac.bigdataetmoi.startup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.database.helper.MapDataHelper;
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
    public void fetchEndroits(Date beginning, Date end) {
        MapDataHelper mapHelper = new MapDataHelper();
        mapHelper.fetchAllLocations(this, beginning.getTime(), end.getTime());
    }

    @Override
    public void fetchApplication() {

    }

    @Override
    public void start() {
        Calendar cal = Calendar.getInstance();

        Date today = cal.getTime();
        cal.add(Calendar.DATE, -10);
        //TODO:Change last week to yesterday --> Debug only
        Date lastWeek = cal.getTime();

        fetchEndroits(lastWeek, today);
    }

    @Override
    public void dataReady(Object o) {
        if(o.getClass() == DataSnapshot.class) {
            DataSnapshot data = (DataSnapshot) o;

            if(!data.hasChildren())
                return;

            for(DataSnapshot child : data.getChildren()) {
                locations.add(child.getValue(LocationData.class));
            }

            if(locations.size() > 0) {
                view.afficherEndroits(locations);
            }
        } else if (o.getClass() == Exception.class) {
            Log.d("BDEM_ERROR", ((Exception)o).getMessage());
        }
    }
}
