/*
  Créé par : Patrick Lapointe
  Le 2018-04-14
  But : Permet d'effectuer les manipulations sur les données de la bd (lecture/écriture)
 */

package ca.uqac.bigdataetmoi.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.uqac.bigdataetmoi.service.info_provider.DataReadyListener;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;



public abstract class AbstractDataManager implements DataReadyListener {

    protected enum DataPath {SENSOR_DATA, CALCULATION_DATA};
    private static DatabaseReference mRootDbRef;
    private static String mCurrentIdentification;
    private ArrayList<DataReadyListener> listeners;

    public AbstractDataManager() {
        if(mRootDbRef == null || mCurrentIdentification == null)
            setRef();

        listeners = new ArrayList<DataReadyListener>();
    }

    private static void setRef()
    {
        mCurrentIdentification = ActivityFetcherActivity.getUserId();
        mRootDbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void dataReady(Object o)
    {
        for(DataReadyListener listener : listeners)
        {
            listener.dataReady(o);
        }
    }

    public void addDataReadyListener(DataReadyListener listener)
    {
        listeners.add(listener);
    }

    public void removeDataReadyListener(DataReadyListener listener)
    {
        listeners.remove(listener);
    }

    private static String getPath(DataPath dataPath){
        String path;

        switch(dataPath) {
            case SENSOR_DATA:
                path = "sensordata/" + mCurrentIdentification;
                break;
            case CALCULATION_DATA:
                path = "calculationdata/" + mCurrentIdentification;
                break;
            default:
                path = "";
        }

        return path;
    }

    private void writeData(String path, Object value) {
        mRootDbRef.child(path).setValue(value);
    }

    /**
     * Ecriture dans la BDD
     *
     * @param dataPath Objet DataPath
     * @param key La cle de l'objet a ecrire
     * @param value l'objet a ecrire
     */
    protected void writeData(DataPath dataPath, String key, Object value) {
        String path = getPath(dataPath) + key;
        writeData(path, value);
    }

    /**
     * Lecture des donnees dans la BDD selon une cle
     *
     * @param dataPath Objet DataPath
     * @param key La cle du noeud a lire
     * @param resultListener Le listener a appeller lorsqu'il y a une erreur ou lorsque les resultats sont prets
     */
    protected void readDataByKey(DataPath dataPath, String key, final ValueEventListener resultListener) {

        mRootDbRef.child(getPath(dataPath)).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultListener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                resultListener.onCancelled(firebaseError);
            }
        });
    }

    /**
     * Lecture des donnees dans la BDD
     *
     * @param dataPath Objet DataPath
     * @param firstKey La cle de debut de filtre
     * @param lastKey La cle de fin de filtre
     * @param resultListener Le listener a appeller lorsqu'il y a une erreur ou lorsque les resultats sont prets
     */
    protected void readDataByRange(DataPath dataPath, String firstKey, String lastKey, final ValueEventListener resultListener) {

        mRootDbRef.child(getPath(dataPath)).orderByKey().startAt(firstKey).endAt(lastKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultListener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                resultListener.onCancelled(firebaseError);
            }        });
    }

}






