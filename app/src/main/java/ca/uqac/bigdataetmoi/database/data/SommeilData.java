package ca.uqac.bigdataetmoi.database.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class SommeilData extends AbstractDataManager implements ValueEventListener {
    private Boolean isSleeping;
    private DataReadyListener listener;
    private boolean fetchingData = false;

    protected static DatabaseReference ref = mRootDbRef.child("sommeil");

    public SommeilData() {}

    /**
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     * @param isSleeping le niveau sonore
     */
    public SommeilData(DataReadyListener listener, Boolean isSleeping)
    {
        this(listener);
        setIsSleeping(isSleeping);
    }

    /**
     * Construit un objet SommeilData sans le niveau sonore de défini
     *
     * @param listener l'objet qui recevera les donnees
     */
    public SommeilData(DataReadyListener listener)
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
        if(isSleeping == null)
            throw new Exception("Object SommeilData incomplete");
        writeData();
    }

    private void setIsSleeping(Boolean isSleeping) {
        this.isSleeping = isSleeping;
    }

    public Boolean getIsSleeping() {
        if(isSleeping == null && !fetchingData) {
            fetchingData = true;
            super.readLastData(ref, this);
            return null;
        } else if(isSleeping == null) {
            return null;
        }
        return isSleeping;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        SommeilData data = dataSnapshot.getValue(SommeilData.class);
        this.setIsSleeping(data.isSleeping);
        this.fetchingData = false;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("BDEM_ERROR", String.format("onCancelled():SommeilData()%n%1$", databaseError.getMessage()));
    }

}
