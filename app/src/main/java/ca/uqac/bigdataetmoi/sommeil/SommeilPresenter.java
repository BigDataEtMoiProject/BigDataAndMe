package ca.uqac.bigdataetmoi.sommeil;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class SommeilPresenter implements ISommeilContract.Presenter, DataReadyListener {

    private ISommeilContract.View view;

    public SommeilPresenter(@NonNull ISommeilContract.View view) {
        if(view != null) {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void dataReady(Object o) {
        if(o.getClass() == DataSnapshot.class) {
            DataSnapshot data = (DataSnapshot) o;

            if(!data.hasChildren())
                return;

            /*
            for(DataSnapshot child : data.getChildren()) {
                locations.add(child.getValue(LocationData.class));
            }

            if(locations.size() > 0) {
                view.afficherEndroits(locations);
            }
            */
        } else if (o.getClass() == Exception.class) {
            Log.d("BDEM_ERROR", ((Exception)o).getMessage());
        }
    }
}
