package ca.uqac.bigdataetmoi.application_usage_details;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.bigdataetmoi.R;

public class ApplicationUsageDetailsFragment extends Fragment implements IApplicationUsageDetailsContract.View{

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        view = inflater.inflate(R.layout.fragment_temps_utilisation_details, container, false);
        setupActionBar();
        return  view;
    }

    private void setupActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Back arrow clicked
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(IApplicationUsageDetailsContract.Presenter presenter) {

    }
}

