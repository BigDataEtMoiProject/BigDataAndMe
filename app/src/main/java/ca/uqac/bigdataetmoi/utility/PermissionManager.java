package ca.uqac.bigdataetmoi.utility;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.MainApplication;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.data_models.PermissionData;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS;

/**
 * Created by Patrick Lapointe on 2018-02-14.
 *
 * But : Créer une base commune pour la gestion des permissions dans l'application.
 * Cette classe nous renseignera si une permission est activée ou non et pourra demander la permission au besoin.
 * Elle pourra aussi désactiver une permission qui avait été donné auparavant.
 *
 * Pour les version d'android avant la 6.0, les permissions étaient demandées lors de l'installation
 * de l'application et étaient maintenues jusqu'à sa désinstallation.
 * À partir de la version 6 d'android, les permission sont demandées lors de l'exécution de l'application.
 * Pour l'application actuelle, nous voulons être capable d'activer/désactiver des permissions de la même facon
 * peut importe la version d'android. Pour ce faire, cette classe va ajouter une gestion des permissions pour
 * permettre l'activation/désactivation des permissions même pour les versions moins récentes d'android.
 */

public class PermissionManager
{
    private static PermissionManager mInstance = null;

    private final static int RQ_ACCESS_FINE_LOCATION = 1;
    private final static int RQ_ACTION_USAGE_ACCESS_SETTINGS = 2;

    List mPermissionsData;

    public static synchronized PermissionManager getInstance()
    {
        if(mInstance == null)
            mInstance = new PermissionManager();
        return mInstance;
    }

    private PermissionManager()
    {
        mPermissionsData = new ArrayList<>();
        getStoredValues();
    }

    // Va chercher les données des permissions dans la bd.
    private void getStoredValues()
    {
        DatabaseManager.getInstance().getDbRef(PermissionData.DATA_ID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPermissionsData.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    PermissionData permissionData = noteDataSnapshot.getValue(PermissionData.class);
                    mPermissionsData.add(permissionData);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // Retourne l'objet stocké selon la permission demandé. Retourne null si rien n'est stocké pour cette permission
    private PermissionData getStoredValue(String permission) {
        PermissionData permissionData = null;

        for(int i = 0 ; i < mPermissionsData.size() && permissionData == null ; i++) {
            if (((PermissionData) mPermissionsData.get(i)).getName().equals(permission))
                permissionData = (PermissionData) mPermissionsData.get(i);
        }

        return permissionData;
    }

    // Nous sommes malheureusement obligé d'assigner une constante numérique pour chaque nom
    // de permission pour lesquel on demande la permission.
    private int getRequestCode(String permission)
    {
        int requestCode;

        switch(permission)
        {
            case ACCESS_FINE_LOCATION:
                requestCode = RQ_ACCESS_FINE_LOCATION;
                break;
            case ACTION_USAGE_ACCESS_SETTINGS:
                requestCode = RQ_ACTION_USAGE_ACCESS_SETTINGS;
                break;
            default:
                requestCode = 0;
        }

        return requestCode;
    }

    // Retourne si oui ou non la permission demandée est accordée.
    // Même si l'application à la permission, on vérifie quand-même si l'utilisateur à désactiver celle-ci dans les options.
    public boolean isGranted(String permission) {
        boolean granted = false;

        if (ContextCompat.checkSelfPermission(MainApplication.getContext(), permission) == PackageManager.PERMISSION_GRANTED)
        {
            PermissionData permissionData = getStoredValue(permission);
            if (permissionData == null)
                granted = true;
            else
                granted = permissionData.getGranted();
        }

        return granted;
    }


    // Demande la permission à l'utilisateur (pour android version 6 et plus)
    // Si nous somme sous android < 6, la permission va déjà être accordée à l'application, donc le popup n'apparaitera pas.
    public void requestPermission(String permission)
    {
        if(!isGranted(permission)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainApplication.getCurrentActivity(), permission))
                ActivityCompat.requestPermissions(MainApplication.getCurrentActivity(),
                        new String[]{permission}, getRequestCode(permission));
        }
    }

    // Met à jour la permission dans la bd selon si oui ou non on veut qu'elle soit activée.
    public void setPermissionGranted(String permission, boolean granted)
    {
        // On demande la permission dans le cas où elle n'est pas déjà donnée
        if(granted)
            requestPermission(permission);

        // TODO : écrire dans la bd selon la valeur voulue.
    }

    // Méthode qui récupère le résultat après une demande de permission. (pour android version 6 et plus)
    // Elle est appelé si une activité qui hérite de BaseActivity recoit une réponse.
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        // Si la permission est acceptée, on met à jour les données dans la bd
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case RQ_ACCESS_FINE_LOCATION:
                    setPermissionGranted(ACCESS_FINE_LOCATION, true);
                    break;
                case RQ_ACTION_USAGE_ACCESS_SETTINGS:
                    setPermissionGranted(ACTION_USAGE_ACCESS_SETTINGS, true);
                    break;
            }
        }
    }
}
