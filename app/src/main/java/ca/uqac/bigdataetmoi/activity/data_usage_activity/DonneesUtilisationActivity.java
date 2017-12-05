package ca.uqac.bigdataetmoi.activity.data_usage_activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.adapter.UsageListAdapter;
import ca.uqac.bigdataetmoi.database.DatabaseManager;
import ca.uqac.bigdataetmoi.database.UsageData;

public class DonneesUtilisationActivity extends AppCompatActivity {

    //Variable for the date picker
    Calendar mCurrentDate;
    Calendar mSelectDate;
    int mYear, mMonth, mDay;

    //Variable for the layout
    MenuItem datePicker;
    MenuItem statPicker;
    TextView mSelectDay;
    TextView mNoData;
    ListView mDialogList;
    ListView mUsageList;
    ProgressBar mProgressBar;
    String[] choices;

    private static final SimpleDateFormat hourMinFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMMM yyyy");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private int index;
    private long sumItemTimeDifference;
    private int mSequence;

    Context mContext;

    //Variable for database management
    DatabaseManager dbManager;
    DatabaseReference usageRef;

    //Variable for the listview
    ArrayList<String> dialogList;
    ListAdapter dialogAdapter;

    ArrayList<UsageData> dispUsageList;
    UsageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donnees_utilisation);

        initializeLayoutAndData();

        sumItemTimeDifference = 0;
        mSequence = 1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_usage, menu);

        //datePicker = menu.findItem(R.id.date_picker);
        //statPicker = menu.findItem(R.id.stats_picker);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.date_picker) {
            getDatePicker();
            return true;
        } else if (id == R.id.stats_picker) {
            getStatPicker();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        readAndDisplaySelectedDate(mCurrentDate);
    }

    public void initializeLayoutAndData() {

        mContext = getApplicationContext();
        //Initialize database manager
        dbManager = DatabaseManager.getInstance();
        usageRef = dbManager.getUsageRef();

        //Display "No data available" only if there no data
        mNoData = (TextView) findViewById(R.id.noData);
        mNoData.setVisibility(View.GONE);

        //Display the load icon before listview appear
        mProgressBar = (ProgressBar) findViewById(R.id.loadingData);
        mProgressBar.setVisibility(View.VISIBLE);

        //Display ic_event and initialize OnClick Method
        //Display selected day
        mCurrentDate = Calendar.getInstance();

        mSelectDay = (TextView) findViewById(R.id.dateSelected);
        mSelectDay.setText(dayMonthFormat.format(mCurrentDate.getTimeInMillis()));


        //Set list and custom
        mUsageList = (ListView) findViewById(R.id.usageList);
        dispUsageList = new ArrayList<>();
        adapter = new UsageListAdapter(this, R.layout.usage_list_layout, dispUsageList);
    }

    public void getDatePicker() {

        //mCurrentDate = Calendar.getInstance();

        mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mMonth = mCurrentDate.get(Calendar.MONTH);
        mYear = mCurrentDate.get(Calendar.YEAR);

        DatePickerDialog dpd = new DatePickerDialog(DonneesUtilisationActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mSelectDate = Calendar.getInstance();
                mSelectDate.set(year, month, dayOfMonth);

                String daySelected = dayMonthFormat.format(mSelectDate.getTimeInMillis());
                mSelectDay.setText(daySelected);

                mProgressBar.setVisibility(View.VISIBLE);
                mUsageList.setVisibility(View.GONE);
                mNoData.setVisibility(View.GONE);
                dispUsageList.clear();

                readAndDisplaySelectedDate(mSelectDate);

                Toast.makeText(mContext , dayMonthFormat.format(mSelectDate.getTimeInMillis()), Toast.LENGTH_SHORT).show();
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

    public void getStatPicker() {

        choices = new String [] {"Timeline", "Details"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose type of stats")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //mDialogList = (ListView) findViewById(R.id.stats_choice);
                        //dialogList = new ArrayList<>();
                        //dialogAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, choices);
                        //mDialogList.setAdapter(dialogAdapter);
                    }
                });
        builder.create();
        builder.show();
    }
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter()


        choices = new String [] {"Timeline", "Details"};
        mDialogList = (ListView) findViewById(R.id.stats_choice);

        dialogList = new ArrayList<>();
        dialogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, choices);
        mDialogList.setAdapter(dialogAdapter);

       /* Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//NO TITLE :)
        dialog.setContentView(android.R.layout.simple_list_item_1);
        dialog.
        dialog.setCancelable(true);
        dialog.show();*/


    private void readAndDisplaySelectedDate(final Calendar cal) {

        usageRef.orderByChild("timeAppEnd").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                index = 0;
                boolean dataExist = false;

                for (DataSnapshot usageSnapshot : dataSnapshot.getChildren())
                {
                    String daySelected = dayMonthFormat.format(cal.getTimeInMillis());
                    String dayAppBegin = getDayFromData(usageSnapshot);

                    if (daySelected.equals(dayAppBegin))
                    {
                        String packageName = (String) usageSnapshot.child("packageName").getValue();
                        long timeAppBegin = (long) usageSnapshot.child("timeAppBegin").getValue();
                        long timeAppEnd = (long) usageSnapshot.child("timeAppEnd").getValue();

                        UsageData usage = new UsageData(packageName, timeAppBegin, timeAppEnd);
                        createListViewForDay(usage);
                        mUsageList.setAdapter(adapter);
                        dataExist = true;
                    }
                }
                if (!dataExist)
                {
                    mNoData.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    dispUsageList.clear();
                }
                else
                    {
                    Collections.reverse(dispUsageList);
                    mUsageList.setVisibility(View.VISIBLE);
                    mNoData.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getDayFromData(DataSnapshot usage) {
        long timeBegin = (long) usage.child("timeAppBegin").getValue();
        return dayMonthFormat.format(timeBegin);
    }

    private void createListViewForDay(UsageData usage) {
        if (index < dispUsageList.size() + 1)               //Make sure the index is inferior to the list size
        {
            if (index == 0) {                                //Insert initial value
                dispUsageList.add(usage);
                index++;
            } else {
                int prevItem = index - 1;

                String currItemPackageName = usage.getPackageName();
                String prevItemPackageName = dispUsageList.get(prevItem).getPackageName();

                //Assure to group item that occured at very close interval
                //Remove groupment where an event occur between the last usage and the current one (like a close screen)
                //long currItemTimeBegin = usage.getTimeAppBegin();
                //long prevItemTimeEnd = dispUsageList.get(prevItem).getTimeAppEnd();
                //long diffLastUsage = currItemTimeBegin - prevItemTimeEnd;

                long currItemTimeDifference = usage.getTimeAppEnd() - usage.getTimeAppBegin();

                if (prevItemPackageName.equals(currItemPackageName)) {
                    if (mSequence == 1) {
                        long prevItemTimeDiff = dispUsageList.get(prevItem).getTimeAppEnd() - dispUsageList.get(prevItem).getTimeAppBegin();
                        sumItemTimeDifference = prevItemTimeDiff;

                    }
                    sumItemTimeDifference = sumItemTimeDifference + currItemTimeDifference;

                    long currItemTimeBegin = usage.getTimeAppBegin();
                    long prevItemTimeEnd = currItemTimeBegin - sumItemTimeDifference;
                    //if (prevItemTimeEnd < 5000) {
                        mSequence++;
                    //}
                } else {
                    if (mSequence == 1) {
                        dispUsageList.add(usage);
                        index++;
                    } else {
                        addGroupItems(prevItem);
                        dispUsageList.add(usage);
                        index++;
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

    public void addGroupItems(int prevIndex) {
        UsageData groupUsage = new UsageData();

        long timeAppBegin = dispUsageList.get(prevIndex).getTimeAppBegin();
        long timeAppEnd = timeAppBegin + sumItemTimeDifference;

        groupUsage.setPackageName(dispUsageList.get(prevIndex).getPackageName());
        groupUsage.setTimeAppBegin(timeAppBegin);
        groupUsage.setTimeAppEnd(timeAppEnd);

        dispUsageList.remove(prevIndex);
        dispUsageList.add(groupUsage);

        mSequence = 1;
        sumItemTimeDifference = 0;
    }

}
