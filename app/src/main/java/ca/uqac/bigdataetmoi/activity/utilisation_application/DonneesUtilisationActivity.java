package ca.uqac.bigdataetmoi.activity.utilisation_application;

import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.BaseActivity;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

@SuppressWarnings("HardCodedStringLiteral")
@RequiresApi(22)
public class DonneesUtilisationActivity extends BaseActivity {

    private static final String TAG = DonneesUtilisationActivity.class.getSimpleName();

    private static final long INIT_TIME_PERIOD = 1000 * 30;     //30 secondes
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA_FRENCH);

    private long mAppStarted, mAppEnded, mPrevUsageTime;
    private String mAppInForeground, mAppInBackground;
    String[] choices;
    Context mContext;

    //Variable pour le  database management
    DatabaseManager dbManager;
    DatabaseReference usageRef;
    UsageStatsManager statsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
            Cette verification est necessaire car la classe UsageStatManager a ete
            implementer avec l'API 21 d'Android.

            A des fin de debugage et de pauvrete, j'ai du desactive la fonctionnalite :(
         */
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 21) {
            setContentView(R.layout.activity_donnees_utilisation);

            //Initialiser et afficher les layouts, donnnées , etc.
            setLayoutAndData();
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Vous devez avoir la version 5.0 (LOLLIPOP) de Android pour utiliser cette fonctionnalite";

            int duree = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duree);
            toast.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_usage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Navigation pour l'Actionbar

        int id = item.getItemId();

        if(id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.stats_picker) {
            getStatPicker();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();

        //Lire la dernière valeur de la base de données
        if(Build.VERSION.SDK_INT >= 21)
            readFirebaseDatabaseForLastValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void setLayoutAndData() {

        mContext = getApplicationContext();

        //Action bar et titre
        setTitle("Phone Usage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Variable
        mAppStarted = mAppEnded = 0;
        mAppInForeground = mAppInBackground = "";
        mPrevUsageTime = 0;

        //Stats Manager pour récolter l'information sur les applications utilisé
        statsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        dbManager = DatabaseManager.getInstance();
        //usageRef = dbManager.getDbRef(UsageData.DATA_ID);

    }

    public void getStatPicker() {

        //Afficher un fenêtre et les choix suivants lorsque l'icone est sur l'action bar est sélectionné
        choices = new String [] {"Timeline", "Details"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose type of stats")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      switch (which) {
                          case 0:
                              //Démarrer activité Timeline
                              startActivity(new Intent(DonneesUtilisationActivity.this, UsageTimelineActivity.class));
                              break;
                          case 1:
                              //TODO: Commencer une nouvelle activité pour l'usage total des applications
                              break;
                      }
                    }
                });
        builder.create();
        builder.show();
    }

    public final void readFirebaseDatabaseForLastValue() {
    /*
        usageRef.limitToLast(1).orderByChild("timeAppEnd").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    UsageData usage  = getLastDataInDatabase(dataSnapshot);
                    UsageEvents events = getTimeRangeEvent(usage);
                    updateUsageDatabase(events);
                }
                else if (!dataSnapshot.exists())
                {
                    UsageData usage  = new UsageData("", 0, 0);
                    UsageEvents events = getTimeRangeEvent(usage);
                    updateUsageDatabase(events);
                }
                else
                {
                    Log.e("ERROR", "SOMETHING WENT WRONG");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
       */
    }

    private UsageData getLastDataInDatabase(DataSnapshot ds) {

        //Obtenir la dernière la dernière données enregistré
        UsageData usage = new UsageData();

        for (DataSnapshot usageSnapshot : ds.getChildren()) {
            long timeAppEnded = (long) usageSnapshot.child("timeAppEnd").getValue();
            usage.setTimeAppEnd(timeAppEnded);
        }

        return usage;
    }

    public final UsageEvents getTimeRangeEvent(UsageData usage) {

        /*
        Obtenir la marge de temps entre le dernier événement
        sauvegardé dans la BD et l'événement courant
        */

        long currentTime = System.currentTimeMillis();
        long lastTime = 0;

        if (usage.getTimeAppEnd() == 0) {
            //Si première utilisation de l'application prendre les événements des 30 dernière secondes
            lastTime = currentTime - INIT_TIME_PERIOD;
        } else {
            lastTime = usage.getTimeAppEnd();
        }

        UsageEvents events;
        events = statsManager.queryEvents(lastTime, currentTime);
        return events;
    }

    public final void updateUsageDatabase(UsageEvents events) {

        /* Mise à jour la base de données en insérant les évènements
         * survenus entre la dernière données enregistré le moment présent
         */

        while (events.hasNextEvent())
        {
            checkUsageEvents(events);
            compareEventsAndInsert();
        }
    }

    public final void checkUsageEvents(UsageEvents events) {

        /* Déterminer si un événement est en avant plan ou arrière plan
        *  et le temps où l'évènement est survenu.
        */

        UsageEvents.Event event = new UsageEvents.Event();
        events.getNextEvent(event);

        //Obtenir les détails quand une application est en avant-plan
        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND)
        {
            //Nom de l'application et time-Stamps
            mAppInForeground = event.getPackageName();
            mAppStarted = event.getTimeStamp();

            // Quand il n'y a plus d'évènement à insérer dans la base données
            // garder en mémoire le time stamp de la dernière application insérer
            // Permet d'assurer une cohérence de données
            if (!events.hasNextEvent()) {
                mPrevUsageTime = mAppStarted;
            }
        }
        //Obtenir les détails quand une applicatioon est en arrière-plan
        else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND)
        {
            mAppInBackground = event.getPackageName();
            mAppEnded = event.getTimeStamp();

            if (!events.hasNextEvent()) {
                mPrevUsageTime = mAppEnded;
            }
        }

    }

    public final void compareEventsAndInsert() {

        /* Comparer les événements en avant-plan et arrière-plan
         * pour insérer dans la BD le moment où l'applicaton à débuté
         * et terminer.
         * Insérer la données dans la BD firebase
         */

        //Événements en avant-plan et arrière-plan doivent avoir le même nom
        if (mAppInForeground.equals(mAppInBackground))
        {
            if (mAppEnded != 0 && mAppStarted != 0)
            {
                long diff = mAppEnded - mAppStarted;

                //Filtrer les événements ayants un temps d'utilisation inférieur à 1 secondes (peu être changé)
                if (diff >= 1000)
                {
                    Log.d(TAG, mAppInForeground + "\t" + "Started at\t" + dateFormat.format(mAppStarted));
                    Log.d(TAG, mAppInBackground + "\t" + "Ended at\t" + dateFormat.format(mAppEnded));
                    Log.d(TAG, mAppInForeground + ":\t" + diff / 1000 + " secondes");

                    UsageData usage = new UsageData();
                    usage.setPackageName(mAppInForeground);
                    usage.setTimeAppBegin(mAppStarted);
                    usage.setTimeAppEnd(mAppEnded);
                    //dbManager.storeSensorData(usage);

                    //Put variable at 0 to assure data a clean
                    mAppStarted = mAppEnded = 0;
                    mAppInForeground = mAppInBackground = "";
                }
            }
        }
    }

}
