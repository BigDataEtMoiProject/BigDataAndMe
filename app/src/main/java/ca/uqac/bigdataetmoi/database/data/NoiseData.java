package ca.uqac.bigdataetmoi.database.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class NoiseData extends AbstractDataManager implements ValueEventListener {
    private Double soundLevel;
    private DataReadyListener listener;
    private boolean fetchingData = false;

    protected static DatabaseReference ref = getRef().child("noise");

    /**
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     * @param soundLevel le niveau sonore
     */
    public NoiseData(DataReadyListener listener, Double soundLevel)
    {
        this(listener);
        setSoundLevel(soundLevel);
    }

    /**
     * Construit un objet NoiseData sans le niveau sonore de défini
     *
     * @param listener l'objet qui recevera les donnees
     */
    public NoiseData(DataReadyListener listener)
    {
        this.listener = listener;
        super.addDataReadyListener(this.listener);
    }

    @Override
    protected void finalize() throws Throwable {
        super.removeDataReadyListener(this.listener);
        super.finalize();
    }

    protected void writeData() {
        super.writeData(ref, this);
    }

    public void checkForWriting() throws Exception {
        if(soundLevel == null)
            throw new Exception("Object NoiseData incomplete");
        writeData();
    }

    private void setSoundLevel(Double soundLevel) {
        this.soundLevel = soundLevel;
    }

    public Double getSoundLevel() {
        if(soundLevel == null && !fetchingData) {
            fetchingData = true;
            super.readLastData(ref, this);
            return null;
        } else if(soundLevel == null) {
            return null;
        }
        return soundLevel;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        NoiseData data = dataSnapshot.getValue(NoiseData.class);
        this.setSoundLevel(data.soundLevel);
        this.fetchingData = false;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("BDEM_ERROR", String.format("onCancelled():NoiseData()%n%1$", databaseError.getMessage()));
    }

}
