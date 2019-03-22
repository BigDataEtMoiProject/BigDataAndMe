package ca.uqac.bigdataetmoi.database.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.lang.annotation.IncompleteAnnotationException;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class LocationData extends AbstractDataManager {
    private Double latitude = null, longitude = null;
    private DataReadyListener listener;
    private boolean fetchingData = false;

    protected static DatabaseReference ref = getRef().child("location");

    /**
     * Lors de la construction de l'objet, les methodes {@link #setLatitude(Double)}) et {@link #setLongitude(Double)}
     * seront automatiquement appeller.
     *
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     * @param latitude la latitude de la position
     * @param longitude la longitude de la position
     */
    public LocationData(DataReadyListener listener, Double latitude, Double longitude)
    {
        this(listener);
        setLatitude(latitude);
        setLongitude(longitude);

    }

    /**
     * Construit un objet LocationData sans latitude ni longitude de predefini
     *
     * Lors de l'appel aux methodes {@link #getLatitude()} et {@link #getLongitude()},
     * l'objet appelera la methode {@link #writeData()}
     *
     * @param listener l'objet qui recevera les donnees
     */
    public LocationData(DataReadyListener listener)
    {
        this.listener = listener;
        super.addDataReadyListener(this.listener);
    }

    /**
     * Utiliser par Firebase pour de-marshaliser les objets dans la BDD
     */
    private LocationData() {}

    @Override
    protected void finalize() throws Throwable {
        super.removeDataReadyListener(this.listener);
        super.finalize();
    }

    protected void writeData() {
        super.writeData(ref, this);
    }

    public void checkForWriting() throws Exception {
        if(longitude == null || latitude == null)
            throw new Exception("Object LocationData incomplete");
        writeData();
    }

    private void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
