package ca.uqac.bigdataetmoi.ui.applications;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.uqac.bigdataetmoi.R;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.models.Application;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {

    List<Application> applicationMessageList = new ArrayList<>();

    public ApplicationAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_textview, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(applicationMessageList.get(position).appName);
    }

    @Override
    public int getItemCount() {
        return applicationMessageList.size();
    }

    public void setData(List<Application> applicationList) {
        this.applicationMessageList = applicationList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.application_textview);
        }

        public void bind(String applicationMessage) {
            textView.setText(applicationMessage);
        }
    }
}
