package ca.uqac.bigdataetmoi.ui.wifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.models.Wifi;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.ui.MainActivity;
import ca.uqac.bigdataetmoi.workers.WifiWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class WifiFragment extends Fragment {

    public static final int REQUEST_ACCESS_WIFI_STATE = 4242;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Wifi> wifis;

    public WifiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (hasAlreadyAcceptedWifiPermission()) {
            startWifiBackgroundWork();
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_wifi, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_wifi_permission, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addWifiPermissionButtonListener();

        if (hasAlreadyAcceptedWifiPermission()) {
            recyclerView = (RecyclerView) getView().findViewById(R.id.my_wifi_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        }

        wifis = new ArrayList<Wifi>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserRepository.getUserFromApi(getActivity()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleUserResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
                Timber.e(t);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_WIFI_STATE) {
            if (hasGrantedWifiPermission(grantResults)) {
                // Display Wifis
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).refreshViewPager();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void addWifiPermissionButtonListener() {
        Button WifiPermissionButton = getView().findViewById(R.id.wifi_continue);

        if (WifiPermissionButton != null) {
            WifiPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForWifiPermission();
                }
            });
        }
    }

    private void askForWifiPermission() {
        Log.d("lyberteam.eu/", "Asking for permissions");
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_ACCESS_WIFI_STATE);
    }

    private void startWifiBackgroundWork() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadWifiWorkRequest = new PeriodicWorkRequest
                .Builder(WifiWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "WIFI WORK",
                        ExistingPeriodicWorkPolicy.KEEP,
                        uploadWifiWorkRequest
                );
    }

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                updateWifi(user);
                Timber.d("§ handleUserResponse");
            }
        } else {
            Timber.e(response.toString());
        }
    }

    public void updateWifi(User user) {
        for (int i = 0; i < user.wifiList.size(); i++) {
            Wifi wifi = user.wifiList.get(i);
            wifis.add(wifi);
        }
        Collections.reverse(wifis);
        if (wifis.size() > 0) {
            wifis.add(0, new Wifi("", wifis.get(0).date, ""));
            for (int i = 1; i < wifis.size(); i++) {
                // if the wifi date changes, insert a header wifi in the arraylist
                if (!wifis.get(i).date.substring(0, 9).equals(wifis.get(i - 1).date.substring(0, 9))) {
                    wifis.add(i, new Wifi("", wifis.get(i).date, ""));
                }
            }
        }

        if (hasAlreadyAcceptedWifiPermission()) {
            mAdapter = new WifiAdapter(wifis, getContext());
            recyclerView.setAdapter(mAdapter);
        }
    }

    private boolean hasAlreadyAcceptedWifiPermission() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasGrantedWifiPermission(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
