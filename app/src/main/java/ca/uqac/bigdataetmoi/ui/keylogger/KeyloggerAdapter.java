package ca.uqac.bigdataetmoi.ui.keylogger;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.uqac.bigdataetmoi.R;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.models.Keylogger;

public class KeyloggerAdapter extends RecyclerView.Adapter<KeyloggerAdapter.MyViewHolder> {

    List<Keylogger> keyloggerMessageList = new ArrayList<>();

    public KeyloggerAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.keylogger_textview, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(keyloggerMessageList.get(position).logged);
    }

    @Override
    public int getItemCount() {
        return keyloggerMessageList.size();
    }

    public void setData(List<Keylogger> keyloggerList) {
        this.keyloggerMessageList = keyloggerList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.keylogger_textview);
        }

        public void bind(String keyloggerMessage) {
            textView.setText(keyloggerMessage);
        }
    }
}
