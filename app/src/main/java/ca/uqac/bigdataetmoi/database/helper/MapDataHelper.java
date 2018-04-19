package ca.uqac.bigdataetmoi.database.helper;

import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.database.AbstractDataManager;
import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;

public class MapDataHelper extends AbstractDataManager implements ValueEventListener {

    private List<LocationData> locations;
    private List<DataReadyListener> listeners;

    public MapDataHelper() {
        locations = new ArrayList<LocationData>();
        listeners = new ArrayList<DataReadyListener>();
    }

    /**
     * Cette methode effectuera un appel a la base de donnee pour lire les donnees a partir
     * d'une date donnee jusqu'a une date donnee
     *
     * ex:
     * en supposant que l'objet appellant implemente l'interface DataReadyListener
     *
     * SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy") // nouvelle date en jour-mois-annee
     * Date beg = formatter.parse("12-12-2012")
     * Date end = formatter.parse("12-24-2012")
     *
     * MapDataHelper.fetchAllLocation(this, beg.getDate(), end.getDate())
     *
     * lorsque les donnees seront recu, la classe DataMapHelper appellera la methode DataReady() de "this"
     *
     * @param listener lecture asychronne dans firebase. On doit prevenir l'objet appellant que les donnees ont ete recupere
     * @param beginning la date de la premiere donnee dans la BDD
     * @param end la date de la derniere donnee dans la BDD
     */
    public void fetchAllLocations(DataReadyListener listener, long beginning, long end)
    {
        listeners.add(listener);
        readDataByRange(AbstractDataManager.DataPath.SENSOR_DATA,
                Long.toString(beginning),
                Long.toString(end),
                this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataReadyListener listener : listeners)
        {
            listener.dataReady(dataSnapshot);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        for(DataReadyListener listener : listeners)
        {
            listener.dataReady(new Exception("MapDataHelper(): Erreur lors de la lecture dans firebase"));
        }
    }
}
