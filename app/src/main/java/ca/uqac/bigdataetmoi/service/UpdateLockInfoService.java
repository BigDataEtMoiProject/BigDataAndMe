package ca.uqac.bigdataetmoi.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Nyemo on 03/11/2017.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class UpdateLockInfoService extends Service {
    boolean oldState = false;
    boolean screenState;
    long begin, useTime;
    BroadcastReceiver mReceiver;
    private File mFile = null;
    FileOutputStream output;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("UpdateService", "Started");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_ANSWER);
        mReceiver = new MyLockReceiver();
        registerReceiver(mReceiver, filter);
       // Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        screenState = intent.getBooleanExtra("screenState", false);
        //Toast.makeText(getApplicationContext(),  "state : " + screenState, Toast.LENGTH_LONG).show();

        if (screenState) // start new period
        {
            begin = System.currentTimeMillis();
            oldState = true;
            useTime = 0;
            //Toast.makeText(getApplicationContext(),  "b : " + begin, Toast.LENGTH_LONG).show();
        } else {
            if (oldState) {
                useTime = System.currentTimeMillis() - begin;
                //Toast.makeText(getApplicationContext(), "Temps : " + useTime, Toast.LENGTH_LONG).show();
                oldState = false;
                Log.i("temps : ", "" + useTime);
                writeInfo();
                //readInfo();

            }


        }


        return super.onStartCommand(intent, flags, startId);

    }

    private void writeInfo() {

        String eol = System.getProperty("line.separator");
        BufferedWriter writer = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA_FRENCH);
        String toDay = sdf.format(new Date());
        //String toDay = new Date().getDate()
        Log.i("date", toDay);
        try {
            writer =
                    new BufferedWriter(new OutputStreamWriter(openFileOutput(toDay,
                            Context.MODE_APPEND)));
            writer.write(useTime + eol);
            //writer.write("This is a test2." + eol);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void readInfo()
    {
        FileInputStream in = null;
        try {
            in = openFileInput( "utilisation.txt");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        InputStreamReader lesTemps = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(lesTemps);
        String unTemps;

        try {
            while((unTemps = reader.readLine()) != null) {
                try {
                    //tempsTotal =+ Integer.parseInt(unTemps);
                    Log.i("untemps", unTemps);
                }
                catch (NumberFormatException e){e.printStackTrace();}
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}
