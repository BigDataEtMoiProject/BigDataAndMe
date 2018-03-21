package ca.uqac.bigdataetmoi.activity.utilisation_application;

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
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;

@SuppressWarnings("HardCodedStringLiteral")
public class UsageListAdapter extends ArrayAdapter<UsageData> {

    private static final SimpleDateFormat hourMinFormat = new SimpleDateFormat("HH:mm:ss", Locale.CANADA_FRENCH);
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

        String convertTime = convertSeconds(diff);

        //Permet l'affichage d'une liste custom
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,   false);       //can cause problem if too many item

        try {
            mApplicationInfo = mPackageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) { }

        TextView hourMinViewEnd = convertView.findViewById(R.id.hourMinView);
        TextView hourMinViewBegin = convertView.findViewById(R.id.hourMinView2);
        ImageView appIconView = convertView.findViewById(R.id.appIconView);
        TextView appNameView = convertView.findViewById(R.id.appNameView);
        TextView timeAppView = convertView.findViewById(R.id.timeAppView);

        hourMinViewEnd.setText(hourMinFormat.format(timeAppEnd));
        hourMinViewBegin.setText(hourMinFormat.format(timeAppBegin));
        appIconView.setImageDrawable(mApplicationInfo.loadIcon(mPackageManager));
        appNameView.setText(mApplicationInfo.loadLabel(mPackageManager));
        timeAppView.setText(convertTime);


        return convertView;
    }

    private static String convertSeconds(long totalSeconds)
    {
        long hours = totalSeconds / 3600;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0)
        {
            return hours + " hours " + minutes + " minutes " + seconds + " secondes";
        }
        else if (minutes > 0)
        {
            return minutes + " minutes " + seconds + " secondes";
        }
        else
        {
            return seconds + " secondes";
        }
    }
}
