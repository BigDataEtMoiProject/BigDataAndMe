package ca.uqac.bigdataetmoi.application_usage;

import java.util.List;

import ca.uqac.bigdataetmoi.database.data.UsageAppData;
import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface IUsageContract {
    interface View extends IBaseView<IUsageContract.Presenter> {
        void displayLastWeekUsage(List<UsageAppData> usageData);
    }

    interface Presenter extends IBasePresenter {
        //Le presenter se chargera de verifier que le ID existe

    }
}
