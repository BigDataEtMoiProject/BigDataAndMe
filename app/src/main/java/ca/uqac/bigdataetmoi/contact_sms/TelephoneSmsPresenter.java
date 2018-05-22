package ca.uqac.bigdataetmoi.contact_sms;


import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.data.ContactData;
import ca.uqac.bigdataetmoi.database.data.SmsData;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

public class TelephoneSmsPresenter implements ITelephoneSms.Presenter{


    private ListView listView; // Vue liste
    private List<String> listInfo; // Liste de stockage pour la liste view
    private ArrayAdapter<String> adapter; // Adapter pour la liste view

    private List<ContactData> contact_list; // Liste des contacts
    private List<SmsData> sms_list; // Liste des SMS

    private AlertDialog.Builder builder; // boite de dialogue
    private PermissionManager permissionManager; // Classe gerant les permissions

    @Override
    public TelephoneSmsPresenter (@NonNull ITelephoneSms.View view) {
        // Initialisation
        listInfo = new ArrayList<String>();
        listView = findViewById(R.id.contactList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listInfo);
        sms_list = new ArrayList<SmsData>();
        permissionManager = PermissionManager.getInstance();

        // On verifie si la permission est OK
        if (permissionManager.isGranted(READ_SMS) && permissionManager.isGranted(READ_CONTACTS)) {

            // * Permission ok on affiche les infos *
            contact_list = getPhoneContacts();
            getPhoneSMS();
            while (!contact_list.isEmpty()) {
                ContactData temp = contact_list.remove(0);
                String tempInfo = temp.getNom() + " -- " + temp.getNumero() + " -- SMS " + temp.getNbrSMSEnvoye();
                listInfo.add(tempInfo);
                listView.setAdapter(adapter);
            }

        } else {

            // * Permission non ok on affiche une boite de dialogue *
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Permission READ_SMS et READ_CONTACTS non accordée.");
            builder.create().show();

        }
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    // Pick up contacts from the phone
    private List<ContactData> getPhoneContacts() {

        // variable resultat
        contact_list = new ArrayList<ContactData>();

        // Recuperation de tous les contacts
        Cursor contacts = getContentResolver().query(
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
            contact_list.add(new ContactDatacontact_id, contact_name, contact_phone_number));

        }

        return contact_list;
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
        Cursor phone_numbers = getContentResolver().query(
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


    // Pick up SMS from the phone
    private List<SmsData> getPhoneSMS() {

        // TODO : Changer le systeme de liaison contact / sms

        // Fonctionnel
        sms_list = new ArrayList<SmsData>();

        Cursor sms_cursor = getContentResolver().query(
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

        return sms_list;

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
            date = formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    @Override
    public void start() {

    }
}
