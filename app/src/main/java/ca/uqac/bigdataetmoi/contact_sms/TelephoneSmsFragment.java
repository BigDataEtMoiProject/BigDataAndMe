package ca.uqac.bigdataetmoi.contact_sms;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.data.ContactData;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;

public class TelephoneSmsFragment extends Fragment implements ITelephoneSmsContract.View {

    private ITelephoneSmsContract.Presenter presenter;
    private ListView listView; // Vue liste

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_telephone_sms, container, false);

        listView = rootView.findViewById(R.id.contactList);

        return rootView;
    }

    @Override
    // On convertie la liste de contacts recue en une liste de string informationelle
    public void setContactList(List<ContactData> contacts) {
        ArrayList<String> listViewContent = new ArrayList<String>();

        while (!contacts.isEmpty()) {
            ContactData temp = contacts.remove(0);
            String tempInfo = temp.getNom() + " -- " + temp.getNumero() + " -- SMS " + temp.getNbrSMSEnvoye();
            listViewContent.add(tempInfo);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityFetcherActivity.getCurrentActivity(), android.R.layout.simple_list_item_1, listViewContent);
        listView.setAdapter(adapter);
    }

    @Override
    // Affiche une boite de dialog contenant l'erreur recue en paramètre
    public void displayError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFetcherActivity.getCurrentActivity());
        builder.setMessage(error);
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null)
            presenter.start();
    }

    @Override
    public void setPresenter(@NonNull ITelephoneSmsContract.Presenter presenter) {
        this.presenter = presenter;
    }

}
