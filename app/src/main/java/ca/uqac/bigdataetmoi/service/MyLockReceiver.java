package ca.uqac.bigdataetmoi.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by Nyemo on 03/11/2017.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class MyLockReceiver extends BroadcastReceiver {

   boolean screenON = true;

    @Override
    public void onReceive(Context context, Intent intent) {
      // Toast.makeText(context,  "un intent : " , Toast.LENGTH_LONG).show();
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
            screenON = true;
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            screenON = false;
        }
        Log.i("state",  "" + screenON);
        //Toast.makeText(context,  "lancer : " + screenON, Toast.LENGTH_LONG).show();
        Intent alerteService = new Intent(context,UpdateLockInfoService.class);
        alerteService.putExtra("screenState", screenON);
        context.startService(alerteService);
    }
}
