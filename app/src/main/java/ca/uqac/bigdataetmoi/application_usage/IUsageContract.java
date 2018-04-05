package ca.uqac.bigdataetmoi.application_usage;

import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface IUsageContract {
    interface View extends IBaseView<Presenter> {

    }

    interface Presenter extends IBasePresenter {

    }
}
