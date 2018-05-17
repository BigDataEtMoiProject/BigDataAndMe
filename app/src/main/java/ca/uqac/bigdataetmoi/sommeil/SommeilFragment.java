package ca.uqac.bigdataetmoi.sommeil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.bigdataetmoi.R;

public class SommeilFragment extends Fragment implements ISommeilContract.View {

    private ISommeilContract.Presenter presenter;

    public SommeilFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View rootView = inflater.inflate(R.layout.fragment_sommeil, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null)
            presenter.start();
    }

    @Override
    public void setPresenter(@NonNull ISommeilContract.Presenter presenter) {
        this.presenter = presenter;
    }

}
