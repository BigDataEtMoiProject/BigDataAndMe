package ca.uqac.bigdataetmoi.startup;

import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface IMainMenuContract {
    interface View extends IBaseView<Presenter> {
        //La view se chargera d'appeller l'activite de detail
        void ouvrirDetailsEndroit(String idLocation);
        void ouvrirDetailsApplication(String idApplication);
        void afficherEndroits();
        void afficherApplication();
    }

    interface Presenter extends IBasePresenter {
        //Le presenter se chargera de verifier que le ID existe
        void ouvrirDetailsEndroit(String idLocation);
        void ouvrirDetailsApplication(String idApplication);
        void fetchEndroits();
        void fetchApplication();
    }
}
