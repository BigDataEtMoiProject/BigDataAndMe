package ca.uqac.bigdataetmoi.startup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public void fetchEndroits(String dateBeginning, String dateEnd) {
        MapDataHelper mapHelper = new MapDataHelper();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date beginning = formatter.parse(dateBeginning);
            Date end = formatter.parse(dateEnd);

            mapHelper.fetchAllLocations(this, beginning.getTime(), end.getTime());
        }catch (ParseException e) {
            Log.d("BDEM EXCEP", e.getMessage());
        }
    }

    @Override
    public void fetchApplication() {

    }

    @Override
    public void start() {
        fetchEndroits("01-04-2018", "18-04-2018");
    }

    @Override
    public void dataReady(Object o) {
        if(o.getClass() == DataSnapshot.class) {
            DataSnapshot data = (DataSnapshot)o;
            //TODO: Reinitialiser la base de donnee pour supprimer les valeurs non conforme (location --> latitude, longitude)
            for(DataSnapshot childData : data.getChildren()) {
                System.out.println(childData.getKey());
                for(DataSnapshot child_childData : childData.getChildren()) {
                    System.out.println("-->"+child_childData.getKey());
                }
            }
        } else if (o.getClass() == Exception.class) {
            Log.d("BDEM EXCEP", ((Exception)o).getMessage());
        }
    }
}
