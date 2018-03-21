package ca.uqac.bigdataetmoi.activity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

public class SommeilActivity extends BaseActivity {

    private TextView mLuxTextView, mInterLuxTextView, mAccelTextView, mProximiTextView;
    DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sommeil);

        //Récupération des views
        mLuxTextView = findViewById(R.id.luxTextView);
        mInterLuxTextView = findViewById(R.id.interLuxTextView);
        mAccelTextView = findViewById(R.id.accelTextView);
        mProximiTextView = findViewById(R.id.proximiTextView);

        //Pour lecture de la bd
        dbManager = DatabaseManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Récupérer les données de la bd (dernier enregistrement
        Query query = dbManager.getSensorDataDbRef().orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // On retrouve la donnée
                if(dataSnapshot.hasChildren()) {
                    DataSnapshot child = dataSnapshot.getChildren().iterator().next();
                    long timestamp = Long.parseLong(child.getKey());
                    DataCollection data = child.getValue(DataCollection.class);

                    // Conversion du timestamp en date
                    Calendar cal = Calendar.getInstance(Locale.CANADA_FRENCH);
                    cal.setTimeInMillis(timestamp);
                    @SuppressWarnings("HardCodedStringLiteral") String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();

                    // On rempli les champs de l'écran
                    mAccelTextView.setText(data.isMoving + getString(R.string.sleep_misc) + date);
                    mLuxTextView.setText(data.luxLevel + getString(R.string.sleep_misc) + date);
                    mInterLuxTextView.setText(interpreterLux(data.luxLevel));
                    mProximiTextView.setText(data.proximityDistance + getString(R.string.sleep_misc) + date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private String interpreterLux(float lux)
    {
        String text;

        if(lux <= 10.0)
            text = getString(R.string.sleep_pitch_black);
        else if(lux <= 30.0)
            text = getString(R.string.sleep_dark);
        else if(lux <= 125)
            text = getString(R.string.sleep_low);
        else if(lux <= 300)
            text = getString(R.string.sleep_med);
        else if(lux <= 700)
            text = getString(R.string.sleep_normal);
        else if(lux <= 7500)
            text = getString(R.string.sleep_bright);
        else
            text = getString(R.string.sleep_really_bright);

        return text;
    }

}

