package ca.uqac.bigdataetmoi.startup;

import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.LocationData;
import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface IMainMenuContract {
    interface View extends IBaseView<Presenter> {
        //La view se chargera d'appeller l'activite de detail
        void ouvrirDetailsEndroit(String idLocation);
        void ouvrirDetailsApplication(String idApplication);
        void afficherEndroits(List<LocationData> loc);
        void afficherApplication();
    }

    interface Presenter extends IBasePresenter {
        //Le presenter se chargera de verifier que le ID existe
        void ouvrirDetailsEndroit(String idLocation);
        void ouvrirDetailsApplication(String idApplication);
        void fetchEndroits(Date dateBeginning, Date dateEnd);
        void fetchApplication();
    }
}
