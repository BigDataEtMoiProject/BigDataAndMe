package ca.uqac.bigdataetmoi.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_RECAP = 2;
    private List<Message> mMessages;
    private Context mContext;

    public MessageAdapter(List<Message> messages, Context mContext) {
        mMessages = messages;
        this.mContext = mContext;
    }

    public void setmMessages(List<Message> mMessages) {
        this.mMessages = mMessages;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_card, parent, false);
            return new VHItem(v);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_header, parent, false);
            return new VHHeader(v);
        } else if (viewType == TYPE_RECAP) {
            //inflate your layout and pass it to view holder
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recap, parent, false);
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
            vhItem.txtPhoneNumber.setText(getItem(position).phone);
            vhItem.txtTime.setText(getItem(position).date.substring(11,16));
            vhItem.txtMessage.setText(getItem(position).message);

        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            VHHeader vhHeader = (VHHeader) holder;

            String date_before = getItem(position).date.substring(0,10);
            String date_after = formateDateFromstring("dd/MM/yyyy", "EEE, d MMM yyyy", date_before);

            date_after = date_after.substring(0, 1).toUpperCase() + date_after.substring(1);
            vhHeader.txtDate.setText(date_after);

        } else if (holder instanceof VHRecap) {
            //cast holder to VHRecap and set data for recap
            VHRecap vhRecap = (VHRecap) holder;
            if (getItem(position).message.equals("0")){
                vhRecap.messageRecapTitle.setText("Aucun élément récupéré");
            }else {
                vhRecap.messageRecapTitle.setText(getItem(position).message + " éléments récupérés");
            }
            String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
            vhRecap.messageRecapDescription.setText("Dernière mise à jour: " + currentTime);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
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
        return mMessages.get(position).phone.equals("header");
    }

    private boolean isPositionRecap(int position) {
        return mMessages.get(position).phone.equals("recap");
    }

    private Message getItem(int position) {
        return mMessages.get(position);
    }

    class VHItem extends RecyclerView.ViewHolder {
        // each data item
        public TextView txtPhoneNumber;
        public TextView txtTime;
        public TextView txtMessage;

        public VHItem(View itemView) {
            super(itemView);
            txtPhoneNumber = itemView.findViewById(R.id.txtPhoneNumber);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtMessage = itemView.findViewById(R.id.txtMessage);
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
        public TextView messageRecapTitle;
        public TextView messageRecapDescription;

        public VHRecap(View itemView) {
            super(itemView);
            messageRecapTitle = itemView.findViewById(R.id.message_recap_title);
            messageRecapDescription = itemView.findViewById(R.id.message_recap_description);
        }
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