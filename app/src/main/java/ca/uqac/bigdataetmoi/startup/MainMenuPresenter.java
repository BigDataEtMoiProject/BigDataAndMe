package ca.uqac.bigdataetmoi.startup;

import android.support.annotation.NonNull;

public class MainMenuPresenter implements IMainMenuContract.Presenter {

    private IMainMenuContract.View view;

    public MainMenuPresenter(@NonNull IMainMenuContract.View view) {
        if(view != null) {
            this.view = view;
            view.setPresenter(this);
        }
    }

    @Override
    public void ouvrirDetailsEndroit(String idLocation) {

    }

    @Override
    public void ouvrirDetailsApplication(String idApplication) {

    }

    @Override
    public void fetchEndroits() {

    }

    @Override
    public void fetchApplication() {

    }

    @Override
    public void start() {
        fetchEndroits();
    }
}
