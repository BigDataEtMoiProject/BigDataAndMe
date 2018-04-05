package ca.uqac.bigdataetmoi.activity.sms_contact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.uqac.bigdataetmoi.R;

class ContactAdapter extends ArrayAdapter<String> {
    private final Context mContext;

    ContactAdapter(Context context, List<String> infos) {
        super(context, R.layout.list_communication_item, infos);
        mContext = context;
    }


    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        final ExerciseHolder holder;

        // On récupère l'élément de la list
        // TODO: change type
        final String contact = getItem(position);

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.list_communication_item, parent, false);
            holder = new ExerciseHolder();
            // Set holder attributes
            holder.name = v.findViewById(R.id.text_contact_name);
            holder.nbMessages = v.findViewById(R.id.text_contact_sms_count);
            v.setTag(holder);
        } else {
            holder = (ExerciseHolder) v.getTag();
        }

        if (contact == null) return v;

        // TODO: Récupérer le nom et le nbr de sms
        /*holder.name.setText(contact.getNom());
        holder.nbMessages.setText(contact.getNbrSMSEnvoye()); */

        return v;
    }

    // ViewHolder pattern
    private static class ExerciseHolder {
        TextView name, nbMessages;
    }
}
