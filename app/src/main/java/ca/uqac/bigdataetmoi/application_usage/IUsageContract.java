package ca.uqac.bigdataetmoi.application_usage;

import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface IUsageContract {
    interface View extends IBaseView<Presenter> {
        //La view se chargera d'appeller l'activite de detail

    }

    interface Presenter extends IBasePresenter {
        //Le presenter se chargera de verifier que le ID existe

    }
}
