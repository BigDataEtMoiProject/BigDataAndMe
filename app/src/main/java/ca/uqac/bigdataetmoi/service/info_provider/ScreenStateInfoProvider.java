package ca.uqac.bigdataetmoi.service.info_provider;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import ca.uqac.bigdataetmoi.database.DataCollection;

/**
 * Created by pat on 2018-03-28.
 */

public class ScreenStateInfoProvider extends InfoProvider
{
    Context mContext;

    public ScreenStateInfoProvider(Context context)
    {
        mContext = context;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PowerManager powerManager = (PowerManager) mContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);

                Boolean screenState = false;
                if(powerManager.isInteractive())
                    screenState = true;

                DataCollection dataCollection = new DataCollection();
                dataCollection.screenState = screenState;
                generateDataReadyEvent(dataCollection);
            }
        }, 5000);
    }
}
