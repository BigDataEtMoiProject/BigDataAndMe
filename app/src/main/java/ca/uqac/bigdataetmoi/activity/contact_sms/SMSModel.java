package ca.uqac.bigdataetmoi.activity.contact_sms;

import android.util.Log;

import java.util.Date;
import java.util.List;

class SMSModel {

    private ContactModel contact;
    private String numero;
    private Date date;

    SMSModel(String _numero, Date _date, List<ContactModel> listeContact) {
        numero = _numero;
        date = _date;
        fetchContact(numero, listeContact);
    }

    private void fetchContact(String numero, List<ContactModel> listeContact) {
        Log.d("Fetch Contact : ", numero);
        int i = 0;
        while (i < listeContact.size() && numero.compareTo(listeContact.get(i).getNumero()) != 0) {
            Log.d("numero contact", listeContact.get(i).getNumero());
            ++i;
        }
        if (i == listeContact.size()) {
            contact = null;
        } else {
            contact = listeContact.get(i);
            listeContact.get(i).addSMSEnvoye(this);
        }
    }

    ContactModel getContactAssocie() {
        return contact;
    }

    String getNumero() {
        return numero;
    }

    Date getDate() {
        return date;
    }

}