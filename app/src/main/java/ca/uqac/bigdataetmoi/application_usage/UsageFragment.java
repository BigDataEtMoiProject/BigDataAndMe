package ca.uqac.bigdataetmoi.application_usage;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.database.data.UsageAppData;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;

@RequiresApi(21)
public class UsageFragment extends Fragment implements IUsageContract.View {

    private IUsageContract.Presenter presenter;
    ListView usageListView;


    public UsageFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedBundle) {
        View rootView = inflater.inflate(R.layout.fragment_donnees_utilisation, container, false);

        if(Build.VERSION.SDK_INT < 21) {
            Context context = getActivity().getApplicationContext();
            CharSequence text = "Vous devez avoir la version 5.0 (LOLLIPOP) de Android pour utiliser cette fonctionnalite";

            int duree = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duree);
            toast.show();
        } else {
            usageListView = rootView.findViewById(R.id.usageList);
        }

        return rootView;
    }

    @Override
    public void displayLastWeekUsage(List<UsageAppData> usageData) {

        ArrayList<String> listViewContent = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy kk:mm");

        for(UsageAppData usageApp : usageData) {
            listViewContent.add(usageApp.getName() + " - " + sdf.format(new Date(usageApp.getLastTimeUse() - 4 * 60 * 60 *1000)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityFetcherActivity.getCurrentActivity(), android.R.layout.simple_list_item_1, listViewContent);
        usageListView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null && Build.VERSION.SDK_INT >= 21)
            presenter.start();
    }

    @Override
    public void setPresenter(IUsageContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
