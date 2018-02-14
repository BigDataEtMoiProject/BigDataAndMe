package ca.uqac.bigdataetmoi.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import ca.uqac.bigdataetmoi.MainApplication;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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
    enum Season { WINTER, SPRING, SUMMER, FALL };

    public static synchronized PermissionManager getInstance()
    {
        if(mInstance == null)
            mInstance = new PermissionManager();
        return mInstance;
    }

    private PermissionManager()
    {

    }

    public boolean isGranted(String permission)
    {
        boolean granted = false;

        if(ContextCompat.checkSelfPermission(MainApplication.getContext(), permission) != PackageManager.PERMISSION_GRANTED)
            granted = true;

        return granted;
    }

    public void requestPermission(String permission)
    {
        if(!isGranted(permission)) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainApplication.getCurrentActivity(), permission))
                ActivityCompat.requestPermissions(MainApplication.getCurrentActivity(), new String[]{permission}, 0);
        }
    }

    // Méthode qui récupère le résultat après une demande de permission.
    // Elle est appelé si une activité qui hérite de BaseActivity recoit une réponse.
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
