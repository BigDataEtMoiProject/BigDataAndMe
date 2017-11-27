package ca.uqac.bigdataetmoi.database;

import java.util.Date;

/**
 * Created by jul_samson on 17-11-17.
 */

public class UsageData {
    private String mPackageName;
    private Date mTimeAppBegin;
    private Date mTimeAppEnd;

    public UsageData() {}

    public UsageData(String packageName, Date appBegin, Date appEnd) {
        mPackageName = packageName;
        mTimeAppBegin = appBegin;
        mTimeAppEnd = appEnd;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public Date getTimeAppBegin() {
        return mTimeAppBegin;
    }

    public void setTimeAppBegin(Date timeAppBegin) {
        this.mTimeAppBegin = timeAppBegin;
    }

    public Date getTimeAppEnd() {
        return mTimeAppEnd;
    }

    public void setTimeAppEnd(Date timeAppEnd) {
        this.mTimeAppEnd = timeAppEnd;
    }
}
