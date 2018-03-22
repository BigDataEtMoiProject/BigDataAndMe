package ca.uqac.bigdataetmoi.activity.contact_sms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime on 22/03/2018.
 */

// MODELE CONTACTS
class ContactModel {
    private String id;
    private String nom;
    private String numero;
    private List<SMSModel> listeSMSEnvoye;

    ContactModel(String _id, String _nom, String _numero) {
        id = _id;
        nom = _nom;
        numero = _numero;
        listeSMSEnvoye = new ArrayList<SMSModel>();
    }

    String getId() {
        return id;
    }

    String getNom() {
        return this.nom;
    }

    String getNumero() {
        return numero;
    }

    void addSMSEnvoye(SMSModel sms) {
        listeSMSEnvoye.add(sms);
    }

    int getNbrSMSEnvoye() {
        return listeSMSEnvoye.size();
    }

}
