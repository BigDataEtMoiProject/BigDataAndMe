package ca.uqac.bigdataetmoi.ui.applications;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.uqac.bigdataetmoi.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.uqac.bigdataetmoi.models.Application;

public class ApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Application> applicationList = new ArrayList<>();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_RECAP = 2;

    public ApplicationAdapter() {
    }

    public void setData(List<Application> applicationList) {
        this.applicationList = applicationList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.application_card, parent, false);
            return new ApplicationAdapter.VHItem(v);
        } else if (viewType == TYPE_HEADER) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.application_header, parent, false);
            return new ApplicationAdapter.VHHeader(v);
        } else if (viewType == TYPE_RECAP) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.application_recap, parent, false);
            return new ApplicationAdapter.VHRecap(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ApplicationAdapter.VHItem) {
            ApplicationAdapter.VHItem vhItem = (ApplicationAdapter.VHItem) holder;
            vhItem.txtAppName.setText(getItem(position).appName);

        } else if (holder instanceof ApplicationAdapter.VHHeader) {
            ApplicationAdapter.VHHeader vhHeader = (ApplicationAdapter.VHHeader) holder;

            String date_before = getItem(position).datetime.substring(0, 10);
            String date_after = formateDateFromstring("dd-MM-yyyy", "EEE, d MMM yyyy", date_before);

            date_after = date_after.substring(0, 1).toUpperCase() + date_after.substring(1);
            vhHeader.txtDate.setText(date_after);

        } else if (holder instanceof ApplicationAdapter.VHRecap) {
            ApplicationAdapter.VHRecap vhRecap = (ApplicationAdapter.VHRecap) holder;
            if (getItem(position).appName.equals("0")) {
                vhRecap.applicationRecapTitle.setText("Aucun élément récupéré");
            } else {
                vhRecap.applicationRecapTitle.setText(getItemCount() + " éléments récupérés");
            }
            String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
            vhRecap.applicationRecapDescription.setText("Dernière mise à jour: " + currentTime);
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
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
        return applicationList.get(position).appName.equals("header");
    }

    private boolean isPositionRecap(int position) {
        return applicationList.get(position).appName.equals("recap");
    }

    private Application getItem(int position) {
        return applicationList.get(position);
    }

    class VHItem extends RecyclerView.ViewHolder {
        public TextView txtAppName;

        public VHItem(View itemView) {
            super(itemView);
            txtAppName = itemView.findViewById(R.id.txtAppName);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        public TextView txtDate;

        public VHHeader(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    class VHRecap extends RecyclerView.ViewHolder {
        public TextView applicationRecapTitle;
        public TextView applicationRecapDescription;

        public VHRecap(View itemView) {
            super(itemView);
            applicationRecapTitle = itemView.findViewById(R.id.application_recap_title);
            applicationRecapDescription = itemView.findViewById(R.id.application_recap_description);
        }
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {
        Date parsed = null;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.FRENCH);
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
            Log.e("exception", "ParseException - dateFormat");
        }
        return outputDate;
    }
}
