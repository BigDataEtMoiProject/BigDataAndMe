package ca.uqac.bigdataetmoi.adapter;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.UsageData;

public class UsageListAdapter extends ArrayAdapter<UsageData> {

    private static final SimpleDateFormat hourMinFormat = new SimpleDateFormat("HH:mm");
    private Context mContext;
    private int mResource;
    private PackageManager mPackageManager;
    private PackageItemInfo mApplicationInfo;

    public UsageListAdapter(@NonNull Context context, int resource, @NonNull List<UsageData> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mPackageManager = context.getPackageManager();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get usag information
        String packageName = getItem(position).getPackageName();
        long timeAppBegin = getItem(position).getTimeAppBegin();
        long timeAppEnd = getItem(position).getTimeAppEnd();

        long diff = (timeAppEnd - timeAppBegin)/1000;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,   false);       //can cause problem if too many item

        try {
            mApplicationInfo = mPackageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) { }
        TextView hourMinView = (TextView)convertView.findViewById(R.id.hourMinView);
        ImageView appIconView = (ImageView)convertView.findViewById(R.id.appIconView);
        TextView appNameView = (TextView)convertView.findViewById(R.id.appNameView);
        TextView timeAppView = (TextView)convertView.findViewById(R.id.timeAppView);

        hourMinView.setText(hourMinFormat.format(timeAppBegin));
        appIconView.setImageDrawable(mApplicationInfo.loadIcon(mPackageManager));
        appNameView.setText(mApplicationInfo.loadLabel(mPackageManager));
        timeAppView.setText(diff + " secondes");

        return convertView;
    }
}
