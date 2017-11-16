package ca.uqac.bigdataetmoi;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TempsUtilisationActivity extends AppCompatActivity {
    TextView useTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temps_utilisation);

       // useTime = findViewById(R.id.textView);
        //useTime.setText("yahoooo");
        ListView myList = findViewById(R.id.listedate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
       // String toDay = sdf.format(new Date());
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        String[] fileList = getFilesDir().list();
        for (int i=0; i < fileList.length; i++)
        {
            HashMap<String, String> element = new HashMap<String, String>();
            int useTime = useTimeDay(fileList[i]);
            element.put("usedate", fileList[i]);
            element.put("usetime", ""+useTime + " minutes");
            liste.add(element);
            //Log.i(fileList[i], "tempdate"+useTime);

        }
        Log.i("laliste", ""+ liste.size());
        ListAdapter adapter = new SimpleAdapter(this, liste, android.R.layout.simple_list_item_2, new String[] {"usedate", "usetime"},
                                            new int[] {android.R.id.text1, android.R.id.text2 });

        myList.setAdapter(adapter);


    }

    protected int useTimeDay(String toDay)
    {
        int tempsTotal = 0;
        try {

            FileInputStream in = openFileInput(toDay);
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
        //useTime.setText("Temps d'utilisation(" + toDay + ") : " + tempsTotal/60000 + " minutes");
        return tempsTotal/60000;
    }

}
