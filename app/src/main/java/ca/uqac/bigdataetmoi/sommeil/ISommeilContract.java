package ca.uqac.bigdataetmoi.sommeil;

import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface ISommeilContract {
    interface View extends IBaseView<ISommeilContract.Presenter> {
    }

    interface Presenter extends IBasePresenter {
    }
}
