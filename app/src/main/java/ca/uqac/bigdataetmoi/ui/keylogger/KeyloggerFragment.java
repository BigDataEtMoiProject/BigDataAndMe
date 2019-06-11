package ca.uqac.bigdataetmoi.ui.keylogger;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;


import java.util.Objects;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static org.osmdroid.api.IMapView.LOGTAG;


public class KeyloggerFragment extends Fragment {

    private RecyclerView keylogger_recycler;
    private KeyloggerAdapter keyloggerAdapter;
    private boolean isDone;


    public KeyloggerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_keylog, container, false);

        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //check if accessibility enable


            Intent intent = new Intent(getContext(), PopUpKeylogger.class);
            startActivity(intent);
            Intent intent2= new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent2, 0);

        if (getView() != null) {
            keyloggerAdapter = new KeyloggerAdapter();
            keylogger_recycler = getView().findViewById(R.id.keylogger_recycler);
            keylogger_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            keylogger_recycler.setAdapter(keyloggerAdapter);
        }

        fetchCurrentUserInfo();
    }

    private void fetchCurrentUserInfo() {
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

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                keyloggerAdapter.setData(user.keyLoggerList);
            }
        } else {
            Timber.e(response.toString());
        }
    }

}




