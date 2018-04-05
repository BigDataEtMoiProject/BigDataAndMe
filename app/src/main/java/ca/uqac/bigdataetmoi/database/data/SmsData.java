package ca.uqac.bigdataetmoi.activity.sms_contact;

import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by Michael on 2018-03-13.
 */
@SuppressWarnings("HardCodedStringLiteral")
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
