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

import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;



public abstract class AbstractDataManager {

    protected enum DataPath {SENSOR_DATA, CALCULATION_DATA};
    private DatabaseReference mRootDbRef;
    private String mCurrentIdentification;

    private AbstractDataManager() {
        mCurrentIdentification = ActivityFetcherActivity.getUserId();
        mRootDbRef = FirebaseDatabase.getInstance().getReference();
    }

    // Fonction qui permet de retourner le chemin associé à un groupe de donnée.
    private String getPath(DataPath dataPath){
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

    // Écriture de la donnée à l'endroit indiqué
    protected void writeData(String path, Object value) {
        mRootDbRef.child(path).setValue(value);
    }

    // Écriture de la donnée dont l'endroit est déterminé automatiquement selon le DataPath
    protected void writeData(DataPath dataPath, String key, Object value) {
        String path = getPath(dataPath) + key;
        writeData(path, value);
    }

    // Lecture d'une donnée selon sa clé
    protected void readDataByKey(DataPath dataPath, String key, final ValueEventListener resultListener) {

        mRootDbRef.child(getPath(dataPath)).equalTo(key).addValueEventListener(new ValueEventListener() {
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

    // Lecture des données selon un "Range"
    protected void readDataByRange(DataPath dataPath, String firstKey, String lastKey, final ValueEventListener resultListener) {

        mRootDbRef.child(getPath(dataPath)).orderByKey().startAt(firstKey).endAt(lastKey).addValueEventListener(new ValueEventListener() {
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

}






