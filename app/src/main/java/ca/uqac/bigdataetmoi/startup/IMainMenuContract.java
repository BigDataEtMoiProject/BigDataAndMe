package ca.uqac.bigdataetmoi.startup;

import com.google.android.gms.maps.GoogleMap;

import ca.uqac.bigdataetmoi.IBasePresenter;
import ca.uqac.bigdataetmoi.IBaseView;

public interface IMainMenuContract {
    interface View extends IBaseView<Presenter> {
        //La view se chargera d'appeller l'activite de detail
        void ouvrirDetaislEndroit(String idLocation);
    }

    interface Presenter extends IBasePresenter {
        //Le presenter se chargera de verifier que le ID existe
        void ouvrirDetailsEndroit(String idLocation);
    }
}
