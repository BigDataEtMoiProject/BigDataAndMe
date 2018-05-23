package ca.uqac.bigdataetmoi.contact_sms;

import ca.uqac.bigdataetmoi.startup.IMainMenuContract;
import ca.uqac.bigdataetmoi.utility.IBaseView;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface ITelephoneSms
{
    interface View extends IBaseView<Presenter>
    {

    }

    interface Presenter extends IBasePresenter
    {

    }
}
