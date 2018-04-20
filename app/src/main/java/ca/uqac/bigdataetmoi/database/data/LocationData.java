package ca.uqac.bigdataetmoi.database.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

/**
 * TODO: Centraliser les donnees dans firebase pour eviter de faire 2 lectures asynchrones
 */
public class LocationData extends AbstractDataManager {
    private Double latitude = null, longitude = null;
    private final String key_lat = "latitude";
    private final String key_long = "longitude";
    private Boolean complete = false;
    private DataReadyListener listener;

    /**
     * Lors de la construction de l'objet, les methodes getLatitude() et getLongitude()
     * seront automatiquement appeller.
     *
     * @param listener Doit s'abonner a l'evenement 'DataReady()' car la lecture dans la BDD est asynchrone.
     *                 Avant de travailler avec l'objet, il faut d'assurer que l'objet est complete.
     */
    public LocationData(DataReadyListener listener)
    {
        this.listener = listener;
        super.addDataReadyListener(this.listener);
        getLatitude();
        getLongitude();
    }

    @Override
    protected void finalize() throws Throwable {
        super.removeDataReadyListener(this.listener);

        super.finalize();
    }

    private void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    private void setLongitude(Double longitude)
    {
        this. longitude = longitude;
    }

    private void checkComplete()
    {
        if(latitude != null && longitude != null)
            complete = true;
    }

    /**
     * Sert a construire l'objet a partir de la base de donnee.
     * Si on a deja la donnee, on la retourne. Sinon, on la li
     * dans la BDD
     *
     * @return La latitude si elle a deja ete lu dans la base de donnee, sinon null
     */
    public Double getLatitude() {
        if(latitude != null)
            return latitude;

        super.readDataByKey("location", "1522332541058", new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot location = dataSnapshot.child(key_lat);
                latitude = (Double) location.getValue();
                setLatitude(latitude);
                checkComplete();
                dataReady(complete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latitude = 0.0;
            }
        });

        return latitude;
    }

    /**
     * Sert a construire l'objet a partir de la base de donnee.
     * Si on a deja la donnee, on la retourne. Sinon, on la li
     * dans la BDD
     *
     * @return La latitude si elle a deja ete lu dans la base de donnee, sinon null
     */
    public Double getLongitude() {
        if(longitude != null)
            return longitude;

        super.readDataByKey("location", "1522332541058", new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot location = dataSnapshot.child(key_long);
                longitude = (Double) location.getValue();
                setLongitude(longitude);
                checkComplete();
                dataReady(complete);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                longitude = null;
            }
        });

        return longitude;
    }

    /**
     * @return True si toutes les donnees ont ete lu dans la BDD, sinon False
     */
    public Boolean isComplete() {
        return complete;
    }
}
