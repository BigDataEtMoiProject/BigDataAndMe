package ca.uqac.bigdataetmoi.activity.sms_contact;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.BaseActivity;
import ca.uqac.bigdataetmoi.utility.PermissionManager;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;

// ACTIVITY
@SuppressWarnings("HardCodedStringLiteral")
public class TelephoneSmsActivity extends BaseActivity {

    private ListView listView;
    private List<String> listInfo;
    private ArrayAdapter<String> adapter;
    private List<ContactModel> contacts;
    private AlertDialog.Builder builder;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_sms);

        listView = findViewById(R.id.contactList);
        listInfo = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listInfo);
        permissionManager = PermissionManager.getInstance();

        // On verifie si la permission est OK
        if(permissionManager.isGranted(READ_SMS) && permissionManager.isGranted(READ_CONTACTS)){

            // * Permission ok on affiche les infos *
            contacts = getPhoneContacts();
            getPhoneSMS();
            while(!contacts.isEmpty())
            {
                ContactModel temp = contacts.remove(0);
                String tempInfo = temp.getNom() + " -- " + temp.getNumero() + " -- SMS " + temp.getNbrSMSEnvoye();
                listInfo.add(tempInfo);
                listView.setAdapter(adapter);
            }

        }else{

            // * Permission non ok on affiche une boite de dialogue *
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Permission READ_SMS et READ_CONTACTS non accordée.");
            builder.create().show();

        }
    }

    protected void onStart() { super.onStart(); }
    protected void onResume()
    {
        super.onResume();
    }


    // Pick up contacts from the phone
    private List<ContactModel> getPhoneContacts()
    {
        List<ContactModel> listeContact = new ArrayList<ContactModel>();

        Cursor tel = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while (tel.moveToNext())
        {
            String nom = tel.getString(tel.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String numeroTel_temp = tel.getString(tel.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String regionCode = numeroTel_temp.substring(1,4);
            String localCode = numeroTel_temp.substring(6,9);
            String numero = numeroTel_temp.substring(10);
            String numeroTel = regionCode+localCode+numero;
            ContactModel temp = new ContactModel(nom, numeroTel);
            listeContact.add(temp);
        }

        return listeContact;
    }

    // Pick up SMS from the phone
    private List<SMSModel> getPhoneSMS()
    {
        List<SMSModel> listeSMS = new ArrayList<SMSModel>();
        Log.d("getPhoneSMS", "Started");

        Cursor smsTel = getContentResolver().query(
                Uri.parse("content://sms/sent"),
                null,
                null,
                null,
                null
        );
        int temp = 0;
        while(smsTel.moveToNext())
        {
            String adresse = smsTel.getString(smsTel.getColumnIndex("address"));
            String date_str = smsTel.getString(smsTel.getColumnIndex("date"));
            Date date = getDate(Long.parseLong(date_str), "yyyyMMdd_HHmmss");
            Log.d("SMS", adresse + " " + date.toString());

            SMSModel sms = new SMSModel(adresse, date, contacts);
            listeSMS.add(sms);
        }
        return listeSMS;
    }

    private Date getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat, Locale.CANADA_FRENCH);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String date_str =  formatter.format(calendar.getTime());

        Date date = null;

        try {
            date = formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
