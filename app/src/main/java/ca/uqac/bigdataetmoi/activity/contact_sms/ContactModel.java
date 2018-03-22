package ca.uqac.bigdataetmoi.activity.contact_sms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime on 22/03/2018.
 */

// MODELE CONTACTS
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

    public String getNom() {return nom;}
    public String getNumero() {return numero;}
    public void addSMSEnvoye(SMSModel sms) {listeSMSEnvoye.add(sms); nbrSMSEnvoye++;}
    public int getNbrSMSEnvoye() {return nbrSMSEnvoye;}
}
