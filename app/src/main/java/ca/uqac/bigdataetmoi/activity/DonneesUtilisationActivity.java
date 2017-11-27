package ca.uqac.bigdataetmoi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.UsageData;

public class DonneesUtilisationActivity extends Activity {

    TextView usageName;
    TextView usageBegin;
    TextView usageEnd;
    ListView mUsageList;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");

    DatabaseManager dbManager;
    DatabaseReference usageRef;

    ArrayList<String> usageArrayList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donnees_utilisation);
        mUsageList = (ListView)findViewById(R.id.usageList);
        dbManager = DatabaseManager.getInstance();
        usageRef = dbManager.getUsageRef();
        usageArrayList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usageArrayList);
        //mUsageList.setAdapter(arrayAdapter);

    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        readDataAndPopulateListView();
    }

    private void readDataAndPopulateListView() {
        usageRef.orderByChild("timeAppEnd").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                UsageData usage = new UsageData();
                for(DataSnapshot usageSnapshot : dataSnapshot.getChildren())
                {
                    if (dataSnapshot.exists()) {
                        Log.e("DATA", "EXIST");
                        String packageName = (String)usageSnapshot.child("packageName").getValue();
                        long hourAppStarted = (long)usageSnapshot.child("timeAppBegin").child("hours").getValue();
                        long minAppStarted = (long)usageSnapshot.child("timeAppBegin").child("minutes").getValue();
                        String info = hourAppStarted + ":" + minAppStarted + "  |   " + packageName;
                        usageArrayList.add(info);
                        mUsageList.setAdapter(arrayAdapter);
                    } else if (!dataSnapshot.exists()) {
                        Log.e("DATA", "DON'T EXIST");
                    } else {
                        Log.e("ERROR", "SOMETHING WENT WRONG");
                    }
                }
                //mUsageList.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
