package ca.uqac.bigdataetmoi.activity.utilisation_application;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.BaseActivity;
import ca.uqac.bigdataetmoi.activity.app_usage_activity.UsageData;
import ca.uqac.bigdataetmoi.activity.utilisation_application.UsageListAdapter;
import ca.uqac.bigdataetmoi.database.DatabaseManager;

@SuppressWarnings("HardCodedStringLiteral")
public class UsageTimelineActivity extends BaseActivity {

    //Variable for the date picker
    Calendar mCurrentDate;
    Calendar mSelectDate;
    int mYear, mMonth, mDay;

    //Variable for the layout
    TextView mSelectDay;
    TextView mNoData;
    ListView mUsageList;
    ProgressBar mProgressBar;

    private static final SimpleDateFormat hourMinFormat = new SimpleDateFormat("HH:mm:ss", Locale.CANADA_FRENCH);
    private static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA_FRENCH);
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.CANADA_FRENCH);
    private int index;
    private int lastIndex;
    private long sumItemTimeDifference;
    private boolean mIsPhoneClose;
    private int mSequence;

    Context mContext;

    //Variable for database management
    DatabaseManager dbManager;
    DatabaseReference usageRef;

    UsageData mPhoneClose;
    ArrayList<UsageData> mItemToGroupList;
    ArrayList<UsageData> dispUsageList;
    UsageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_timeline);
        setTitle("Timeline");
        initializeLayoutAndData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_usage_timeline, menu);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e("Error", "The action bar is not set");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.date_picker) {
            getDatePicker();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
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
        //usageRef = dbManager.getDbRef(UsageData.DATA_ID);

        ActionBar actionBar = getActionBar();

        //Display "No data available" only if there no data
        mNoData = findViewById(R.id.noData);
        mNoData.setVisibility(View.GONE);

        //Display the load icon before listview appear
        mProgressBar = findViewById(R.id.loadingData);
        mProgressBar.setVisibility(View.VISIBLE);

        //Display ic_event and initialize OnClick Method
        //Display selected day
        mCurrentDate = Calendar.getInstance();

        mSelectDay = findViewById(R.id.dateSelected);
        mSelectDay.setText(dayMonthFormat.format(mCurrentDate.getTimeInMillis()));

        mIsPhoneClose = false;
        mItemToGroupList = new ArrayList<>();

        //Set list and custom
        mUsageList = findViewById(R.id.usageList);
        dispUsageList = new ArrayList<>();
        adapter = new UsageListAdapter(this, R.layout.usage_list_layout, dispUsageList);

        sumItemTimeDifference = 0;
        mSequence = 1;
    }

    public void getDatePicker() {

        mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mMonth = mCurrentDate.get(Calendar.MONTH);
        mYear = mCurrentDate.get(Calendar.YEAR);

        DatePickerDialog dpd = new DatePickerDialog(UsageTimelineActivity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
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

    private void readAndDisplaySelectedDate(final Calendar cal) {
/*
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
                        createListViewForSelectDay(usage);      //Create listview under certain condition
                        mUsageList.setAdapter(adapter);         //Set adapter to display listview
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
*/
    }

    private String getDayFromData(DataSnapshot usage) {
        long timeBegin = (long) usage.child("timeAppBegin").getValue();
        return dayMonthFormat.format(timeBegin);
    }

    private void createListViewForSelectDay(UsageData usage) {

        if (index < dispUsageList.size() + 1)               //Make sure the index is inferior to the list size
        {
            if (index == 0)
            {                                //Insert initial value
                dispUsageList.add(usage);
                mItemToGroupList.add(usage);
                index++;
            }
            else {
                int prevItem = index - 1;

                String currItemPackageName = usage.getPackageName();
                String prevItemPackageName = dispUsageList.get(prevItem).getPackageName();
                long prevItemTimeDiff;

                long currItemTimeDifference = usage.getTimeAppEnd() - usage.getTimeAppBegin();

                if (prevItemPackageName.equals(currItemPackageName))
                {
                    if (mSequence == 1)
                    {
                        prevItemTimeDiff = dispUsageList.get(prevItem).getTimeAppEnd() - dispUsageList.get(prevItem).getTimeAppBegin();
                        sumItemTimeDifference = prevItemTimeDiff;
                        mItemToGroupList.add(usage);
                    }
                    sumItemTimeDifference = sumItemTimeDifference + currItemTimeDifference;
                    mItemToGroupList.add(usage);

                    mSequence++;

                } else {

                    if (mSequence == 1)
                    {
                        dispUsageList.add(usage);
                        mItemToGroupList.clear();
                        index++;
                    }
                    else
                    {
                        groupItemsToOne(prevItem);
                        dispUsageList.add(usage);
                        mItemToGroupList.clear();
                        index++;
                    }
                }
            }
        }
        else
        {
            Log.e("Index:", ":" + index);
            Log.e("Size:", ":" + dispUsageList.size());
            Log.e("Error:", "Index is out of bound");
        }
    }

    public void groupItemsToOne(int prevIndex) {
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
