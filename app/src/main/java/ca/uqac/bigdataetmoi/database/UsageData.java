package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by jul_samson on 17-11-16.
 */

public class UsageData {
    Date mDateAppStart;
    Date mDateAppEnd;
    String mPackageName;
    long mDiffInSecond;

    public UsageData (String packageName, Date start, Date end, long diff) {
        mDateAppStart = start;
        mDateAppEnd = end;
        mPackageName = packageName;
        mDiffInSecond = diff;
    }

}
