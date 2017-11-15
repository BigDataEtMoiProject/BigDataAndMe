package ca.uqac.bigdataetmoi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TempsUtilisationActivity extends AppCompatActivity {
    TextView useTime = null;
    int tempsTotal = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temps_utilisation);

        useTime = findViewById(R.id.textView);
        useTime.setText("yahoooo");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String toDay = sdf.format(new Date());
        try {

            FileInputStream in = openFileInput( toDay);
            InputStreamReader lesTemps = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(lesTemps);
            String unTemps;

            while((unTemps = reader.readLine()) != null) {
                try {
                    tempsTotal += Integer.parseInt(unTemps);
                    Log.i("untemps", unTemps);
                }
                catch (NumberFormatException e){e.printStackTrace();}
            }
            if(in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        useTime.setText("Temps d'utilisation(" + toDay + ") : " + tempsTotal/60000 + " minutes");
    }
}
