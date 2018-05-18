package ca.uqac.bigdataetmoi.sommeil;

import ca.uqac.bigdataetmoi.utility.IBasePresenter;
import ca.uqac.bigdataetmoi.utility.IBaseView;

public interface ISommeilContract {
    interface View extends IBaseView<ISommeilContract.Presenter> {
        void afficherMoyenneMoisDernier(double moyenne);
        void afficherMoyenneSemaineDern(double moyenne);
        void afficherTempsHier(double temps);
    }

    interface Presenter extends IBasePresenter {
        void fetchSommeilInfo(long debut, long fin);
    }
}
