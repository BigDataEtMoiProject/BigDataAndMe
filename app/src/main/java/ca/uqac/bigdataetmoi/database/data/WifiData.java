package ca.uqac.bigdataetmoi.database.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class WifiData extends AbstractDataManager implements ValueEventListener {    
    private List<String> wifiSSID;
    private DataReadyListener listener;
    private boolean fetchingData = false;

    protected static DatabaseReference ref = getRef().child("wifi");

    /**
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     * @param wifiSSID la liste des ssid wifi à proximité
     */
    public WifiData(DataReadyListener listener, List<String> wifiSSID)
    {
        this(listener);
        setWifiSSID(wifiSSID);
    }

    /**
     * Construit un objet WifiData sans ssids de défini
     *
     * @param listener l'objet qui recevera les donnees
     */
    public WifiData(DataReadyListener listener)
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
        if(wifiSSID == null)
            throw new Exception("Object WifiData incomplete");
        writeData();
    }

    private void setWifiSSID(List<String> wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public List<String> getWifiSSID() {
        if(wifiSSID == null && !fetchingData) {
            fetchingData = true;
            readLastData(ref, this);
            return null;
        } else if(wifiSSID == null) {
            return null;
        }
        return wifiSSID;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        WifiData data = dataSnapshot.getValue(WifiData.class);
        this.setWifiSSID(data.wifiSSID);
        this.fetchingData = false;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("BDEM_ERROR", String.format("onCancelled():WifiData()%n%1$", databaseError.getMessage()));
    }

}
