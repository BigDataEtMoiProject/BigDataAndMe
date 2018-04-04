package ca.uqac.bigdataetmoi.utility;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.bigdataetmoi.startup.ActivityFetcher;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

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

    private final static boolean DEFAULT_VALUE = true; // Valeur par défaut des permissions

    private List<PermissionChangedListener> listeners = new ArrayList<>();

    private DatabaseManager dbManager;
    private Map<String, Boolean> permissionMap;

    public static synchronized PermissionManager getInstance() {
        if(mInstance == null)
            mInstance = new PermissionManager();
        return mInstance;
    }

    private PermissionManager() {
        dbManager = DatabaseManager.getInstance();
        getStoredValues();
    }

    public void addListener(PermissionChangedListener toAdd) {
        listeners.add(toAdd);
    }

    // Va chercher les données des permissions dans la bd.
    private void getStoredValues() {
        permissionMap = new HashMap<>();

        dbManager.getPermissionDbRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                permissionMap.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    permissionMap.put(data.getKey().replace('-', '.'), data.getValue(Boolean.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // Demande la permission à l'utilisateur (pour android version 6 et plus)
    private void requestPermission(String permission) {
            ActivityCompat.requestPermissions(ActivityFetcher.getCurrentActivity(), new String[]{permission}, 0);
    }

    // Retourne si oui ou non la permission demandée est accordée.
    // Même si l'application à la permission, on vérifie quand-même si l'utilisateur à désactiver celle-ci dans les options.
    public boolean isGranted(String permission) {
        boolean granted = false;

        if (ContextCompat.checkSelfPermission(ActivityFetcher.getCurrentActivity(), permission) == PackageManager.PERMISSION_GRANTED)
        {
            if (permissionMap.get(permission) == null)
                granted = DEFAULT_VALUE;
            else
                granted = permissionMap.get(permission);
        }

        return granted;
    }

    // Met à jour la permission dans la bd selon si oui ou non on veut qu'elle soit activée.
    public void setPermissionGranted(String permission, boolean granted) {
        // Dans un premier temps, il faut s'assurer de demander la permission à l'usager
        // Via le popup de permission android si nous somme dans la version android 6.0 et plus
        // Si l'usager accepte d'accorder la permission, cette fonction sera appelé une deuxième fois,
        // mais le traitement sera différent vue que la permission sera accordée.
        if(granted && ContextCompat.checkSelfPermission(ActivityFetcher.getContext(), permission) == PackageManager.PERMISSION_DENIED)
            requestPermission(permission);
        else {
            dbManager.getPermissionDbRef().child(permission.replace('.', '-')).setValue(granted);
            permissionMap.put(permission,granted);
            for (PermissionChangedListener listener : listeners)
                listener.permissionChanged(permission, granted);
        }
    }

    // Méthode qui récupère le résultat après une demande de permission. (pour android version 6 et plus)
    // Elle est appelé si une activité qui hérite de BaseActivity recoit une réponse.
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Si la permission est acceptée, on met à jour les données dans la bd
        for(int i = 0 ; i < grantResults.length ; i++)
        {
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                setPermissionGranted(permissions[i], true);
        }
    }
}
