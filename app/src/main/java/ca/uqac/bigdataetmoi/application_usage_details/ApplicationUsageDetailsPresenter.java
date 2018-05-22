package ca.uqac.bigdataetmoi.application_usage_details;

import android.support.annotation.NonNull;

public class ApplicationUsageDetailsPresenter implements IApplicationUsageDetailsContract.Presenter{

    private IApplicationUsageDetailsContract.View view;

    public  ApplicationUsageDetailsPresenter(@NonNull IApplicationUsageDetailsContract.View view) {
        if (view != null) {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void start() {

    }
}
