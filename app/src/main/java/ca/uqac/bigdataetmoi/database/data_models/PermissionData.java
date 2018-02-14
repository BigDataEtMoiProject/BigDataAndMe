package ca.uqac.bigdataetmoi.database.data_models;

/**
 * Created by Patrick Lapointe on 2018-02-14.
 *
 * But : Stockage des données relatives aux permission accordées ou non.
 *
 */

public class PermissionData implements Data {
    public static final String DATA_ID = "permissiondata";
    String mName;
    boolean mGranted;

    public PermissionData(String name, boolean granted)
    {
        mName = name;
        mGranted = granted;
    }

    public String getName() { return mName; }
    public boolean getGranted() { return mGranted; }

    @Override
    public String getDataID() { return DATA_ID; }
}
