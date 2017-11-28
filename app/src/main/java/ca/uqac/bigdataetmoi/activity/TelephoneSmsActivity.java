package ca.uqac.bigdataetmoi.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.internal.nu;

import ca.uqac.bigdataetmoi.Manifest;
import ca.uqac.bigdataetmoi.R;

class ContactModel{
    private String nom;
    private String numero;
    private List<SMSModel> listeSMSEnvoye;
    private int nbrSMSEnvoye;

    ContactModel (String _nom,  String _numero)
    {
        nom = _nom;
        numero = _numero;
        nbrSMSEnvoye = 0;
        listeSMSEnvoye = new ArrayList<SMSModel>();
    }

    public String getNom()
    {return nom;}
    public String getNumero()
    {return numero;}
    public void addSMSEnvoye(SMSModel sms)
    {listeSMSEnvoye.add(sms); nbrSMSEnvoye++;}
    public int getNbrSMSEnvoye()
    {return nbrSMSEnvoye;}
}

class SMSModel{
    private ContactModel contactAssocie;
    private String numero;
    private Date date;

    SMSModel(String _numero, Date _date, List<ContactModel> listeContact)
    {
        numero = _numero;
        date = _date;
        fetchContact(numero, listeContact);
    }
    public ContactModel getContactAssocie() {return contactAssocie;}
    public String getNumero(){return numero;}
    public Date getDate(){return date;}
    private void fetchContact(String numero, List<ContactModel> listeContact)
    {
        Log.d("fetchContact", numero);
        int i = 0;
        while(i < listeContact.size() && numero.compareTo(listeContact.get(i).getNumero()) != 0)
        {
            Log.d("numero contact", listeContact.get(i).getNumero());
            ++i;
        }
        if(i == listeContact.size())
        {
            contactAssocie = null;
        } else
        {
            contactAssocie = listeContact.get(i);
            listeContact.get(i).addSMSEnvoye(this);
        }
    }
}

public class TelephoneSmsActivity extends AppCompatActivity {

    private SmsManager smsMan;
    private ListView listView;
    private List<String> listInfo;
    private ArrayAdapter<String> adapter;
    private final int PERMISSION_CODE = 0x0800;
    private List<ContactModel> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_sms);

        listView = findViewById(R.id.contactList);
        listInfo = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listInfo);

        int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS");
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CONTACTS"}, PERMISSION_CODE);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.READ_SMS");
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS"}, PERMISSION_CODE);
        }

        contacts = getPhoneContacts();
        getPhoneSMS();
        while(!contacts.isEmpty())
        {
            ContactModel temp = contacts.remove(0);
            String tempInfo = temp.getNom() + " -- " + temp.getNumero() + " -- SMS " + temp.getNbrSMSEnvoye();
            listInfo.add(tempInfo);
            listView.setAdapter(adapter);
        }
    }

    protected void onStart() { super.onStart(); }

    protected void onResume()
    {
        super.onResume();
    }

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
            Date date = getDate(Long.parseLong(date_str), "yyyy/MM/dd hh:mm:ss");
            Log.d("SMS", adresse + " " + date.toString());

            SMSModel sms = new SMSModel(adresse, date, contacts);
            listeSMS.add(sms);
        }
        return listeSMS;
    }

    private Date getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

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
