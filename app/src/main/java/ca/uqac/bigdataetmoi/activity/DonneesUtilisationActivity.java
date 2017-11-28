package ca.uqac.bigdataetmoi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.adapter.UsageListAdapter;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.UsageData;

public class DonneesUtilisationActivity extends Activity {

    TextView mToday;
    ListView mUsageList;
    ProgressBar mProgressBar;

    private static final SimpleDateFormat hourMinFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMMM yyyy");
    private String today;
    private int index;
    private long timeGroupStart;
    private long timeGroupEnd;
    private long diffTotal;
    private int nbSameNameSeq;

    DatabaseManager dbManager;
    DatabaseReference usageRef;

    ArrayList<UsageData> dispUsageList;
    UsageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donnees_utilisation);

        mProgressBar = (ProgressBar)findViewById(R.id.loadingData);
        mProgressBar.setVisibility(View.VISIBLE);

        mToday = (TextView) findViewById(R.id.today);
        mUsageList = (ListView) findViewById(R.id.usageList);

        dbManager = DatabaseManager.getInstance();
        usageRef = dbManager.getUsageRef();

        dispUsageList = new ArrayList<>();
        adapter = new UsageListAdapter(this, R.layout.usage_list_layout, dispUsageList);

        timeGroupStart = 0;
        timeGroupEnd = 0;
        diffTotal = 0;
        nbSameNameSeq = 1;

        long currentTime = System.currentTimeMillis();
        today = dayMonthFormat.format(currentTime);
        mToday.setText(today);


    }

    public void onStart() {
        super.onStart();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void onResume() {
        super.onResume();
        readAndPopulateListView();
    }

    private void readAndPopulateListView()
    {
        usageRef.orderByChild("timeAppEnd").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long currentTime = System.currentTimeMillis();
                String today = dayMonthFormat.format(currentTime);

                dispUsageList.clear();

                for (DataSnapshot usageSnapshot : dataSnapshot.getChildren())
                {
                    if (dataSnapshot.exists())
                    {
                        long timeAppBegin = (long) usageSnapshot.child("timeAppBegin").getValue();

                        String dayAppBegin = dayMonthFormat.format(timeAppBegin);

                        if (today.equals(dayAppBegin)) {
                            String packageName = (String) usageSnapshot.child("packageName").getValue();
                            long timeAppEnd = (long) usageSnapshot.child("timeAppEnd").getValue();
                            UsageData usage = new UsageData(packageName, timeAppBegin, timeAppEnd);
                            createListView(usage);
                        }
                    } else if (!dataSnapshot.exists()) {
                        Log.e("DATA", "DON'T EXIST");
                    } else {
                        Log.e("ERROR", "SOMETHING WENT WRONG");
                    }
                }
                Collections.reverse(dispUsageList);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void createListView(UsageData usage)
    {
        int prevItem = index - 1;

        long diff = usage.getTimeAppEnd()- usage.getTimeAppBegin();

        if (index < dispUsageList.size() + 1)               //Make sure the index is inferior to the list size
        {
            if(index == 0) {                                //Insert initial value
                dispUsageList.add(usage);
                mUsageList.setAdapter(adapter);
            } else {
                String currPackageName = usage.getPackageName();
                String prevPackageName = dispUsageList.get(prevItem).getPackageName();


                if (prevPackageName.equals(currPackageName)) {
                    /*if(nbSameNameSeq == 1) {
                        long prevItemDiff =
                        diffTotal = diffTotal + getPrevItemDiff(prevItem);
                        Log.d("CHK:", nbSameNameSeq + ": " + packageName + " " + diff/1000 + " " + diffTotal/1000);
                    }
                    diffTotal = diffTotal + diff;
                    nbSameNameSeq++;
                    Log.d("CHK:", nbSameNameSeq + ": " + packageName + " " + diff/1000 + " " + diffTotal/1000);*/
                } else {
                    if (nbSameNameSeq == 1) {
                        populateSingleItemListView(prevItem);
                    } else {
                        populateSingleItemListView(prevItem);
                       // populateMultiItemListView();
                    }
                }
                //UsageData prevUsage = getPrevItem(prevItem);
                //dispUsageList.add(prevUsage);
                //mUsageList.setAdapter(adapter);
            }   //DELETE INDEX 0 ONCE COMPLETE POPULATED

        } else {
            Log.e("Index:", ":" + index);
            Log.e("Size:", ":" + dispUsageList.size());
            Log.e("Error:", "Index is out of bound");
        }
    }

    private UsageData getPrevItem(int prevIndex) {
        UsageData prevUsage = new UsageData();
        prevUsage.setPackageName(dispUsageList.get(prevIndex).getPackageName());
        prevUsage.setTimeAppBegin(dispUsageList.get(prevIndex).getTimeAppBegin());
        prevUsage.setTimeAppEnd(dispUsageList.get(prevIndex).getTimeAppEnd());
        return prevUsage;
    }

    /*private void initTodayListView ()
    {
        int index = 0;
        for (int i = 0; i <= dataUsageList.size(); i++)
        {
            //UsageData prevItem = dataUsageList.get(index - 1);
            UsageData currItem = dataUsageList.get(index);
            UsageData nextItem = dataUsageList.get(index + 1);
            Log.d("Index:", "" + index);
            if (currItem.getPackageName().equals(nextItem.getPackageName())) {
                long diff = currItem.getTimeAppEnd() - currItem.getTimeAppBegin();
                nbSameNameSeq++;
                diffTotal = diffTotal + diff;

            } else if (!currItem.getPackageName().equals(nextItem.getPackageName())) {

                if (nbSameNameSeq > 0) { //check previous instead
                    long diff = currItem.getTimeAppEnd() - currItem.getTimeAppBegin();
                    diffTotal = diffTotal + diff;

                    timeGroupStart = currItem.getTimeAppEnd() - diffTotal;
                } else {
                    timeGroupStart = currItem.getTimeAppBegin();
                }
                timeGroupEnd = currItem.getTimeAppEnd();

                UsageData groupedUsage = new UsageData(currItem.getPackageName(), timeGroupStart, timeGroupEnd);
                dispUsageList.add(groupedUsage);
                mUsageList.setAdapter(adapter);
            } else {
                Log.e("Error:", "Something when wrong");
            }
            index++;
        }
    }*/
    /*
    private void groupSameNameSequence( int index) {

        long diff = timeAppEnd - timeAppStart;
        int prevItem = index - 1;



        if (index < dataUsageList.size() + 1)               //Make sure the index is inferior to the list size
        {
            if (prevItem >= 0)                          //Make sure
            {

                if(checkIfCurrentNameDifferentFromPrevious(prevItem, packageName)) {
                    if(nbSameNameSeq == 0) {
                        diffTotal = diffTotal + getPrevItemDiff(prevItem);
                        Log.d("CHK:", nbSameNameSeq + ": " + packageName + " " + diff/1000 + " " + diffTotal/1000);
                    }
                    diffTotal = diffTotal + diff;
                    nbSameNameSeq++;
                    Log.d("CHK:", nbSameNameSeq + ": " + packageName + " " + diff/1000 + " " + diffTotal/1000);
                } else if(!checkIfCurrentNameDifferentFromPrevious(prevItem, packageName)) {
                    Log.d("SEQ:", nbSameNameSeq + ": " + packageName + " " + diff/1000 + " " + diffTotal/1000);
                    nbSameNameSeq = 0;
                    diffTotal = 0;

                }
               //CREATE THE COMPLETE LIST
                //CREATE A SUBLIST WHICH WILL BE DISPLAY
/*
                timeGroupStart = timeAppEnd - diffTotal;
                timeGroupEnd = timeAppEnd;
                Log.d("SAME:", packageName + " appears " + timeGroupStart + " - " + timeGroupEnd);
                if (nbSameNameSeq == 0){
                   populateSingleItemListView(index, packageName, timeAppStart, timeAppEnd);
                } else {
                    populateMultiItemListView(index, prevItem, dataUsageList.get(prevItem).getPackageName(), timeGroupStart, timeGroupEnd);
                }/*
                if(nbSameNameSeq  0)
                timeGroupStart = 0;
                timeGroupEnd = 0;
                nbSameNameSeq = 0;
                diffTotal = 0;
*//*
            } else {
                Log.e("Error:", "Previous item refer to negative index");
            }
            usage = new UsageData(packageName, new Date(timeAppStart), new Date(timeAppEnd));
            dataUsageList.add(usage);
            mUsageList.setAdapter(adapter);
        } else {
            Log.e("Index:", ":" + index);
            Log.e("Size:", ":" + dataUsageList.size());
            Log.e("Error:", "Index is out of bound");
        }
    }*/

    public void populateSingleItemListView(int index)
    {
        UsageData usage = getPrevItem(index);
        Log.d(""+ nbSameNameSeq,
                usage.getPackageName() + " " +
                        hourMinFormat.format(usage.getTimeAppBegin()) + " " +
                        hourMinFormat.format(usage.getTimeAppEnd()));
        dispUsageList.add(usage);
        mUsageList.setAdapter(adapter);
    }

    public void populateMultiItemListView(int index, int prevItem, String name, long begin, long end) {
        Log.d("" + nbSameNameSeq,   name + " " + hourMinFormat.format(begin) + " " + hourMinFormat.format(end));

        List<UsageData> dataSubList = dispUsageList.subList(index-nbSameNameSeq, index);
        begin = dataSubList.get(0).getTimeAppBegin();
        for(int i = 0; i<dataSubList.size(); i++) {
            UsageData usage = new UsageData(name, begin, end);
            end = dataSubList.get(i).getTimeAppEnd();
        }
        UsageData usage = new UsageData(name, begin, end);
        dispUsageList.add(usage);
        mUsageList.setAdapter(adapter);
        dataSubList.clear();
    }
}

        /*
        usageList.subList(index,index-n)
        if (index == 0 || !usageList.get(index-1).getPackageName().equals(packageName))
        {
            /if(n > 0) {
                int checkGroupFirstIndex = index - n;
                timeGroupStart = usageList.get(checkGroupFirstIndex).getTimeAppBegin().getTime();
                usageList.remove(checkGroupFirstIndex);
                usage.setPackageName(packageName);
                usage.setTimeAppBegin(new Date(timeGroupStart));
                usage.setTimeAppEnd(new Date(timeGroupEnd));
                usageList.add(usage);
            } else {
                usage.setPackageName(packageName);
                usage.setTimeAppBegin(new Date(timeAppStart));
                usage.setTimeAppEnd(new Date(timeAppEnd));
                usageList.add(usage);
                mUsageList.setAdapter(adapter);
           // }
            index++;
        } else {
            n++;
            diffTotal = diffTotal + diff;
            timeGroupStart = timeAppEnd - diffTotal;
            timeGroupEnd = timeAppEnd;

            Log.d("Start:", packageName +"Group usage value: " + hourMinFormat.format(timeGroupStart));
            Log.d("Ended:", packageName +"Group usage value: " + hourMinFormat.format(timeGroupEnd));
        }*/
