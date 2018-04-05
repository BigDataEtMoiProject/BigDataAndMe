package ca.uqac.bigdataetmoi.localisation;

import android.app.Fragment;

public class LocalisationFragment extends Fragment implements ILocalisationContract.View {
    private ILocalisationContract.Presenter presenter;

    @Override
    public void setPresenter(ILocalisationContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
