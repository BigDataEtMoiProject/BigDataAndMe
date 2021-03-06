package ca.uqac.bigdataetmoi.ui.wifi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.Wifi;

public class WifiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_RECAP = 2;
    private List<Wifi> mWifis;
    private Context mContext;

    public WifiAdapter(List<Wifi> wifis, Context mContext) {
        mWifis = wifis;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.wifi_card, parent, false);
            return new VHItem(v);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.wifi_header, parent, false);
            return new VHHeader(v);
        } else if (viewType == TYPE_RECAP) {
            //inflate your layout and pass it to view holder
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.wifi_recap, parent, false);
            return new VHRecap(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHItem) {
            //cast holder to VHItem and set data
            VHItem vhItem = (VHItem) holder;
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            vhItem.txtSsid.setText(getItem(position).ssid);
            vhItem.txtDate.setText(getItem(position).date.substring(11,16));
            vhItem.txtName.setText(getItem(position).name);

        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            VHHeader vhHeader = (VHHeader) holder;

            String date_before = getItem(position).date.substring(0,10);
            String date_after = WifiAdapter.formateDateFromstring("dd/MM/yyyy", "EEE, d MMM yyyy", date_before);

            date_after = date_after.substring(0, 1).toUpperCase() + date_after.substring(1);
            vhHeader.txtDate.setText(date_after);
        } else if (holder instanceof VHRecap) {
            //cast holder to VHRecap and set data for recap
            VHRecap vhRecap = (VHRecap) holder;
            if (getItem(position).date.equals("0")){
                vhRecap.wifiRecapTitle.setText("Aucun élément récupéré");
            } else {
                vhRecap.wifiRecapTitle.setText(getItem(position).name + " éléments récupérés");
            }
            String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
            vhRecap.wifiRecapDescription.setText("Dernière mise à jour: " + currentTime);
        }
    }

    @Override
    public int getItemCount() {
        return mWifis.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        if (isPositionRecap(position))
            return TYPE_RECAP;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        // isHeader if position == 0 or if phone == header
        return mWifis.get(position).ssid.equals("header");
    }

    private boolean isPositionRecap(int position) {
        return mWifis.get(position).ssid.equals("recap");
    }

    private Wifi getItem(int position) {
        return mWifis.get(position);
    }

    class VHItem extends RecyclerView.ViewHolder {
        // each data item
        public TextView txtSsid;
        public TextView txtName;
        public TextView txtDate;

        public VHItem(View itemView) {
            super(itemView);
            txtSsid = itemView.findViewById(R.id.txtSsid);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }

    class VHRecap extends RecyclerView.ViewHolder {
        public TextView wifiRecapTitle;
        public TextView wifiRecapDescription;

        public VHRecap(View itemView) {
            super(itemView);
            wifiRecapTitle = itemView.findViewById(R.id.wifi_recap_title);
            wifiRecapDescription = itemView.findViewById(R.id.wifi_recap_description);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        public TextView txtDate;

        public VHHeader(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    public void setmWifi(List<Wifi> mWifis) {
        this.mWifis = mWifis;
        notifyDataSetChanged();
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){
        Date parsed = null;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.FRENCH);
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
            Log.e("exeption", "ParseException - dateFormat");
        }
        return outputDate;
    }
}
