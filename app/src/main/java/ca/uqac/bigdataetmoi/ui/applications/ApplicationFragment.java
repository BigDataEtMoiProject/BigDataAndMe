package ca.uqac.bigdataetmoi.ui.applications;


import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.events.OnApplicationUploadedEvent;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.workers.ApplicationWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import ca.uqac.bigdataetmoi.models.Application;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationFragment extends Fragment {

    private RecyclerView application_recycler;
    private TextView myTextView;
    private ApplicationAdapter applicationAdapter;

    public ApplicationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startApplicationUploadingBackgroundWork();
        return inflater.inflate(R.layout.fragment_application, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() != null) {
            applicationAdapter = new ApplicationAdapter();
            application_recycler = getView().findViewById(R.id.application_recycler);
            myTextView = getView().findViewById(R.id.application_textview);
            application_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            application_recycler.setAdapter(applicationAdapter);
        }

        fetchCurrentUserInfo();
    }

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                Collections.reverse(user.timeOnAppList);
                adaptApplicationList(user.timeOnAppList);
                if (user.timeOnAppList.size() > 0) {
                    myTextView.setVisibility(View.GONE);
                    application_recycler.setVisibility(View.VISIBLE);
                }
                applicationAdapter.setData(user.timeOnAppList);
            }
        } else {
            Timber.e(response.toString());
        }
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

    public void adaptApplicationList(List<Application> applicationList){
        applicationList.add(0, new Application("recap", ""));
        if (applicationList.size() > 1) {
            applicationList.add(1, new Application("header", applicationList.get(1).datetime));
            for (int i = 2; i < applicationList.size(); i++) {
                if (!applicationList.get(i).datetime.substring(0, 9).equals(applicationList.get(i - 1).datetime.substring(0, 9))) {
                    applicationList.add(i, new Application("header", applicationList.get(i).datetime));
                }
            }
        }
    }

    private void startApplicationUploadingBackgroundWork() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadLocationWorkRequest = new PeriodicWorkRequest
                .Builder(ApplicationWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "APPLICATION WORK",
                        ExistingPeriodicWorkPolicy.KEEP,
                        uploadLocationWorkRequest
                );
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplicationUploaded(OnApplicationUploadedEvent event) {
        User user = event.getUser();
        Collections.reverse(user.timeOnAppList);
        adaptApplicationList(user.timeOnAppList);
        if (user.timeOnAppList.size() > 0) {
            myTextView.setVisibility(View.GONE);
            application_recycler.setVisibility(View.VISIBLE);
        }
        applicationAdapter.setData(user.timeOnAppList);
    }

}
