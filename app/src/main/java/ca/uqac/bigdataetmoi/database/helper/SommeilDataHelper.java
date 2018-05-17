package ca.uqac.bigdataetmoi.database.helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.database.data.SommeilData;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class SommeilDataHelper extends AbstractDataManager implements ValueEventListener {

    private List<SommeilData> sommeilData;
    private List<DataReadyListener> listeners;
    private DatabaseReference ref = mRootDbRef.child("sommeil");

    public SommeilDataHelper() {
        sommeilData = new ArrayList<SommeilData>();
        listeners = new ArrayList<DataReadyListener>();
    }

    public void fetchAllSommeilData(DataReadyListener listener, long beginning, long end)
    {
        listeners.add(listener);
        readDataByRange(ref,
                Long.toString(beginning),
                Long.toString(end),
                this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataReadyListener listener : listeners) {
            listener.dataReady(dataSnapshot);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        for(DataReadyListener listener : listeners) {
            listener.dataReady(new Exception("MapDataHelper(): Erreur lors de la lecture dans firebase"));
        }
    }

}
