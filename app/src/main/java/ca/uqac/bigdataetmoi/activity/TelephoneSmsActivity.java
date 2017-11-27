package ca.uqac.bigdataetmoi.activity;

import java.util.ArrayList;
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

import ca.uqac.bigdataetmoi.Manifest;
import ca.uqac.bigdataetmoi.R;

class ContactModel{
    private String nom;
    private String numero;

    ContactModel (String _nom,  String _numero)
    {
        nom = _nom;
        numero = _numero;
    }

    String getNom()
    {return nom;}
    String getNumero()
    {return numero;}
}

class SMSModel{
    private ContactModel contactAssocie;

    SMSModel(String numero, List<ContactModel> listeContact){fetchContact(numero, listeContact);}
    public void fetchContact(String numero, List<ContactModel> listeContact)
    {
        int i = 0;
        while(i < listeContact.size() && numero != listeContact.get(i).getNumero())
        {
            ++i;
        }
        if(i == listeContact.size())
        {
            contactAssocie = null;
        } else
        {
            contactAssocie = listeContact.get(i);
        }
    }
}

public class TelephoneSmsActivity extends AppCompatActivity {

    private SmsManager smsMan;
    private ListView listView;
    private List<String> listInfo;
    private ArrayAdapter<String> adapter;
    private final int PERMISSION_CODE = 0x0800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_sms);

        listView = findViewById(R.id.contactList);
        listInfo = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listInfo);
    }

    protected void onStart()
    {
        super.onStart();

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

        List<ContactModel> contacts = getPhoneContacts();
        getPhoneSMS();
        while(!contacts.isEmpty())
        {
            ContactModel temp = contacts.remove(0);
            String tempInfo = temp.getNom() + " -- " + temp.getNumero();
            listInfo.add(tempInfo);
            listView.setAdapter(adapter);
        }
    };

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
            String numeroTel = tel.getString(tel.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

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
                Uri.parse("content://sms/outbox"),
                null,
                null,
                null,
                null
        );
        Log.d("While statement", "Begin");
        int temp = 0;
        while(smsTel.moveToNext())
        {
            Log.d("While statement", "Loop "+temp);
            for (int i = 0; i < smsTel.getColumnCount(); i++) {
                Log.d(smsTel.getColumnName(i) + "", smsTel.getString(i) + "");
            }
            Log.d("One row finished",
                    "**************************************************");
            ++temp;
        }
        return listeSMS;
    }
}
