/*
  Créé par : Patrick Lapointe
  Le 2018-04-14
  But : Permet d'effectuer les manipulations sur les données de la bd (lecture/écriture)
 */

package ca.uqac.bigdataetmoi.database;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    protected static DatabaseReference mRootDbRef;
    protected static long currentTimeMillis = System.currentTimeMillis();
    private ArrayList<DataReadyListener> listeners;

    public AbstractDataManager() {
        if(mRootDbRef == null)
            setRef();

        listeners = new ArrayList<DataReadyListener>();
    }

    private static void setRef()
    {
        mRootDbRef = FirebaseDatabase.getInstance().
                getReference().child("data");
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

    /**
     * Ecriture dans la BDD du temps actuel
     */
    protected void writeData(DatabaseReference ref, Object c) {
        ref.child(Long.toString(System.currentTimeMillis())).setValue(c);
    }

    /**
     * Lecture des donnees dans la BDD selon une cle
     *
     * @param ref la reference au sous-arbre contenant la donnée (exemple : location)
     * @param key La cle du noeud a lire
     * @param resultListener Le listener a appeller lorsqu'il y a une erreur ou lorsque les resultats sont prets
     */
    protected void readDataByKey(DatabaseReference ref, String key, final ValueEventListener resultListener) {
        ref.child(key).addValueEventListener(new ValueEventListener() {
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
     * @param ref la reference au sous-arbre contenant la donnée (exemple : location)
     * @param firstKey La cle de debut de filtre
     * @param lastKey La cle de fin de filtre
     * @param resultListener Le listener a appeller lorsqu'il y a une erreur ou lorsque les resultats sont prets
     */
    protected void readDataByRange(DatabaseReference ref, String firstKey, String lastKey, final ValueEventListener resultListener) {

        ref.orderByKey().startAt(firstKey).endAt(lastKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultListener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                resultListener.onCancelled(firebaseError);
            }        });
    }

    protected void readLastData(DatabaseReference ref, final ValueEventListener listener) {
        ref.orderByKey().limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }
}






