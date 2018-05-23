package ca.uqac.bigdataetmoi.contact_sms;

import android.support.annotation.NonNull;

public class TelephoneSmsPresenter implements ITelephoneSms.Presenter{


    private ITelephoneSms.View view;

    public TelephoneSmsPresenter (@NonNull ITelephoneSms.View view) {
        if(view != null)
        {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void start() {

    }
}
