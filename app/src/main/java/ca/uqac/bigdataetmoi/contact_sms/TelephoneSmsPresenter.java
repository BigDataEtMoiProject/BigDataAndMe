package ca.uqac.bigdataetmoi.contact_sms;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.ContactData;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;

public class TelephoneSmsPresenter implements ITelephoneSmsContract.Presenter{


    private ITelephoneSmsContract.View view;

    public TelephoneSmsPresenter (@NonNull ITelephoneSmsContract.View view) {
        if(view != null)
        {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void start() {
        PermissionManager permissionManager = PermissionManager.getInstance();

        if (permissionManager.isGranted(READ_SMS) && permissionManager.isGranted(READ_CONTACTS))
            fetchContacts();
        else
            view.displayError("Permission READ_SMS et READ_CONTACTS non accordée.");
    }

    @Override
    public void fetchContacts() {
        // variable resultat
        List<ContactData> contact_list = new ArrayList<ContactData>();

        // Recuperation de tous les contacts
        Cursor contacts = ActivityFetcherActivity.getCurrentActivity().getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Parcour du curseur resultat
        while (contacts.moveToNext()) {

            // Recuperation de l'id
            String contact_id = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
            Log.i("INFO : ", contact_id);

            // Recuperation du nom
            String contact_name = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("INFO : ", contact_name);

            // Recuperation du numero de telephone associé
            String contact_phone_number = getPhoneNumberById(contact_id);
            Log.i("INFO : ", contact_phone_number);

            // Ajout du contact dans la liste des contacts
            contact_list.add(new ContactData(contact_id, contact_name, contact_phone_number));
        }

        view.setContactList(contact_list);
    }

    // On recupere le numero de telephone a partir d'un contact ID
    private String getPhoneNumberById(String contact_id) {

        // Variable resultat
        String phone_number;

        // Recuperation de l'id et du numero de téléphone
        String[] mProjection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // Selection par contact ID
        String mSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

        // Argument
        String[] mSelectionArgs = {
                contact_id
        };

        // Recuperation du numero de téléphone associé au numéro du contact
        Cursor phone_numbers = ActivityFetcherActivity.getCurrentActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                mProjection,
                mSelection,
                mSelectionArgs,
                null
        );

        // On recupere le premier numéro celui inscrit sur le téléphone ( hors application tiers )
        phone_numbers.moveToFirst();
        phone_number = phone_numbers.getString(phone_numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        phone_numbers.close();

        return phone_number;
    }

}
