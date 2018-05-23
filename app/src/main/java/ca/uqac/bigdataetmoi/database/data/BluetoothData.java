package ca.uqac.bigdataetmoi.database.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class BluetoothData extends AbstractDataManager implements ValueEventListener {
    private List<String> bluetoothSSID;
    private DataReadyListener listener;
    private boolean fetchingData = false;

    protected static DatabaseReference ref = getRef().child("bluetooth");

    /**
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     * @param bluetoothSSID la liste des ssid bluetooth à proximité
     */
    public BluetoothData(DataReadyListener listener, List<String> bluetoothSSID)
    {
        this(listener);
        setBluetoothSSID(bluetoothSSID);
    }

    /**
     * Construit un objet BluetoothData sans ssids de défini
     *
     * @param listener l'objet qui recevera les donnees
     */
    public BluetoothData(DataReadyListener listener)
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
        if(bluetoothSSID == null)
            throw new Exception("Object BluetoothData incomplete");
        writeData();
    }

    private void setBluetoothSSID(List<String> bluetoothSSID) {
        this.bluetoothSSID = bluetoothSSID;
    }

    public List<String> getBluetoothSSID() {
        if(bluetoothSSID == null && !fetchingData) {
            fetchingData = true;
            super.readLastData(ref, this);
            return null;
        } else if(bluetoothSSID == null) {
            return null;
        }
        return bluetoothSSID;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BluetoothData data = dataSnapshot.getValue(BluetoothData.class);
        this.setBluetoothSSID(data.bluetoothSSID);
        this.fetchingData = false;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d("BDEM_ERROR", String.format("onCancelled():BluetoothData()%n%1$", databaseError.getMessage()));
    }
}
