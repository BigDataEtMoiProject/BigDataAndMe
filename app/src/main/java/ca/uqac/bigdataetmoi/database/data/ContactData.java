package ca.uqac.bigdataetmoi.database.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2018-03-13.
 */
public class ContactData {
    private String id;
    private String nom;
    private String numero;
    private List<SmsData> listeSMSEnvoye;

    public ContactData(String _id, String _nom, String _numero) {
        id = _id;
        nom = _nom;
        numero = _numero;
        listeSMSEnvoye = new ArrayList<SmsData>();
    }

    String getId() {
        return id;
    }

    public String getNom() {
        return this.nom;
    }

    public String getNumero() {
        return numero;
    }

    void addSMSEnvoye(SmsData sms) {
        listeSMSEnvoye.add(sms);
    }

    public int getNbrSMSEnvoye() {
        return listeSMSEnvoye.size();
    }
}
