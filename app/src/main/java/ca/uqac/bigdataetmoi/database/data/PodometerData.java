package ca.uqac.bigdataetmoi.database.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class PodometerData extends AbstractDataManager implements ValueEventListener {
    private Float steps;
    private DataReadyListener listener;
    private boolean fetchingData = false;

    protected static DatabaseReference ref = getRef().child("podometer");

    /**
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     * @param steps le niveau sonore
     */
    public PodometerData(DataReadyListener listener, Float steps)
    {
        this(listener);
        setSteps(steps);
    }

    /**
     * Construit un objet PodometerData sans le niveau sonore de défini
     *
     * @param listener l'objet qui recevera les donnees
     */
    public PodometerData(DataReadyListener listener)
    {
        this.listener = listener;
        super.addDataReadyListener(this.listener);
    }

    @Override
    protected void finalize() throws Throwable {
        super.removeDataReadyListener(this.listener);
        super.finalize();
    }

    public static void readLastData(final ValueEventListener listener) {
        AbstractDataManager.readLastData(ref, listener);
    }

    protected void writeData() {
        super.writeData(ref, this);
    }

    public void checkForWriting() throws Exception {
        if(steps == null)
            throw new Exception("Object PodometerData incomplete");
        writeData();
    }

    private void setSteps(Float steps) {
        this.steps = steps;
    }

    public Float getSteps() {
        if(steps == null && !fetchingData) {
            fetchingData = true;
            super.readLastData(ref, this);
            return null;
        } else if(steps == null) {
            return null;
        }
        return steps;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        PodometerData data = dataSnapshot.getValue(PodometerData.class);
        this.setSteps(data.steps);
        this.fetchingData = false;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("BDEM_ERROR", String.format("onCancelled():PodometerData()%n%1$", databaseError.getMessage()));
    }
}
