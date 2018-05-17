package ca.uqac.bigdataetmoi.sommeil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import ca.uqac.bigdataetmoi.R;

public class SommeilFragment extends Fragment implements ISommeilContract.View {

    private ISommeilContract.Presenter presenter;

    private TextView moisDernierTextView, semDernTextView, hierTextView;

    public SommeilFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View rootView = inflater.inflate(R.layout.fragment_sommeil, container, false);

        moisDernierTextView = rootView.findViewById(R.id.moisDernierTextView);
        semDernTextView = rootView.findViewById(R.id.semDernTextView);
        hierTextView = rootView.findViewById(R.id.hierTextView);

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

    @Override
    public void afficherMoyenneMoisDernier(double moyenne) {
        DecimalFormat df = new DecimalFormat("#.##");
        moisDernierTextView.setText(df.format(moyenne) +  " h");
    }

    @Override
    public void afficherMoyenneSemaineDern(double moyenne) {
        DecimalFormat df = new DecimalFormat("#.##");
        semDernTextView.setText(df.format(moyenne) + " h");
    }

    @Override
    public void afficherTempsHier(double temps) {
        DecimalFormat df = new DecimalFormat("#.##");
        hierTextView.setText(df.format(temps) + " h");
    }

}
