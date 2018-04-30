package ca.uqac.bigdataetmoi.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Patrick Lapointe on 2017-11-14.
 * Cette classe sert spécifiquement à démarrer le service principal lors de l'ouverture du système.
 */

public class BroadcastOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            BigDataService.startRecurrence(context.getApplicationContext());
        }
    }
}
