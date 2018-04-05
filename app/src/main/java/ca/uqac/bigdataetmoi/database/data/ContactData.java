package ca.uqac.bigdataetmoi.database.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2018-03-13.
 */
public class ContactData {
    private String nom;
    private String numero;
    private List<SmsData> listeSMSEnvoye;
    private int nbrSMSEnvoye;

    public ContactData(String _nom, String _numero)
    {
        nom = _nom;
        numero = _numero;
        nbrSMSEnvoye = 0;
        listeSMSEnvoye = new ArrayList<SmsData>();
    }

    public String getNom()
    {return nom;}
    public String getNumero()
    {return numero;}
    public void addSMSEnvoye(SmsData sms)
    {listeSMSEnvoye.add(sms); nbrSMSEnvoye++;}
    public int getNbrSMSEnvoye()
    {return nbrSMSEnvoye;}
}
