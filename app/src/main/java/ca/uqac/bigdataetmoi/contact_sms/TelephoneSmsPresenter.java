package ca.uqac.bigdataetmoi.contact_sms;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.ContactData;
import ca.uqac.bigdataetmoi.database.data.SmsData;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.utility.PermissionManager;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;

public class TelephoneSmsPresenter implements ITelephoneSmsContract.Presenter {


    private ITelephoneSmsContract.View view;

    public TelephoneSmsPresenter(@NonNull ITelephoneSmsContract.View view) {
        if (view != null) {
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
        getPhoneSMS(contact_list);
        view.setContactList(contact_list);
    }

    private void getPhoneSMS(List<ContactData> contact_list) {

        // TODO : Changer le systeme de liaison contact / sms

        // Fonctionnel
        List<SmsData> sms_list = new ArrayList<SmsData>();

        Cursor sms_cursor = ActivityFetcherActivity.getCurrentActivity().getContentResolver().query(
                Uri.parse("content://sms/sent"),
                null,
                null,
                null,
                null
        );


        while (sms_cursor.moveToNext()) {
            String adresse = sms_cursor.getString(sms_cursor.getColumnIndex("address"));
            String date_str = sms_cursor.getString(sms_cursor.getColumnIndex("date"));
            Date date = getDate(Long.parseLong(date_str), "yyyy/MM/dd hh:mm:ss");
            Log.d("SMS", adresse + " " + date.toString());

            SmsData sms = new SmsData(adresse, date, contact_list);
            sms_list.add(sms);
        }

    }

    private Date getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String date_str = formatter.format(calendar.getTime());

        Date date = null;

        try {
            date = (Date) formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
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

        if (phone_numbers != null && phone_numbers.moveToFirst()) {
            int index = phone_numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phone_number = phone_numbers.getString(index);
            phone_numbers.close();
        } else {
            phone_number = "";
        }

        return phone_number;
    }

}
