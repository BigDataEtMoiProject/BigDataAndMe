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

    private static DatabaseReference mRootDbRef;
    private ArrayList<DataReadyListener> listeners;

    public AbstractDataManager() {
        if(mRootDbRef == null)
            setRef();

        listeners = new ArrayList<DataReadyListener>();
    }

    private static void setRef()
    {
        mRootDbRef = FirebaseDatabase.getInstance().
                getReference().child(ActivityFetcherActivity.getUserId()).child("data");
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

    private void writeData(Object value) { mRootDbRef.setValue(value); }

    /**
     * Ecriture dans la BDD
     *
     * @param dataType le nom du sous-arbre contenant la donnée (exemple : location)
     * @param key La cle de l'objet a ecrire (exemple : un timestamp)
     * @param value l'objet a ecrire
     */
    protected void writeData(String dataType, String key, Object value) {
        mRootDbRef.child(dataType).child(key).setValue(value);
    }

    /**
     * Lecture des donnees dans la BDD selon une cle
     *
     * @param dataType le nom du sous-arbre contenant la donnée (exemple : location)
     * @param key La cle du noeud a lire
     * @param resultListener Le listener a appeller lorsqu'il y a une erreur ou lorsque les resultats sont prets
     */
    protected void readDataByKey(String dataType, String key, final ValueEventListener resultListener) {

        mRootDbRef.child(dataType).child(key).addValueEventListener(new ValueEventListener() {
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
     * @param dataType le nom du sous-arbre contenant la donnée (exemple : location)
     * @param firstKey La cle de debut de filtre
     * @param lastKey La cle de fin de filtre
     * @param resultListener Le listener a appeller lorsqu'il y a une erreur ou lorsque les resultats sont prets
     */
    protected void readDataByRange(String dataType, String firstKey, String lastKey, final ValueEventListener resultListener) {

        mRootDbRef.child(dataType).orderByChild("timestamp").startAt(firstKey).endAt(lastKey).addValueEventListener(new ValueEventListener() {
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






