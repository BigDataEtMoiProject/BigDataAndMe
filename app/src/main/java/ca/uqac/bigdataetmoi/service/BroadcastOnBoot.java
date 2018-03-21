package ca.uqac.bigdataetmoi.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.uqac.bigdataetmoi.service.BigDataService;

/**
 * Created by Patrick Lapointe on 2017-11-14.
 * Cette classe sert spécifiquement à démarrer le service principal lors de l'ouverture du système.
 */

public class BroadcastOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            BigDataService.startRecurrence(context.getApplicationContext());
        }
    }
}
