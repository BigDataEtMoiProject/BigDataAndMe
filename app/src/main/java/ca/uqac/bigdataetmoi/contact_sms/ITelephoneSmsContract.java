package ca.uqac.bigdataetmoi.contact_sms;

import ca.uqac.bigdataetmoi.database.data.ContactData;
import ca.uqac.bigdataetmoi.startup.IMainMenuContract;
import ca.uqac.bigdataetmoi.utility.IBaseView;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface ITelephoneSmsContract
{
    interface View extends IBaseView<Presenter>
    {
        void setContactList(List<ContactData> contacts);
        void displayError(String error);
    }

    interface Presenter extends IBasePresenter
    {
        void fetchContacts();
    }
}
