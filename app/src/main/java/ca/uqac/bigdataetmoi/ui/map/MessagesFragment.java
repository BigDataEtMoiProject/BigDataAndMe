package ca.uqac.bigdataetmoi.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.adapters.MessageAdapter;
import ca.uqac.bigdataetmoi.data.services.UserService;
import ca.uqac.bigdataetmoi.data.services.HttpClient;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.models.Message;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.workers.LocationWorker;
import ca.uqac.bigdataetmoi.workers.MessageWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MessageDto> messages;


    public MessagesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startMessageBackgroundWork();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) getView().findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        messages = new ArrayList<>();
        messages.add(new MessageDto("0777777777", "10/04/2019 16:33:54","blablabla blalblabla"));
        messages.add(new MessageDto("0888888888", "10/04/2019 16:33:54","test" ));
        messages.add(new MessageDto("0733937393", "07/04/2019 16:33:54","test2"));
        // todo: call API to get Messages and fill the arrayList
        // todo: include empty objects in the arrayList which represent headers
        if(messages.size() > 0) {
            messages.add(0, new MessageDto("header", messages.get(0).date, ""));
                for (int i = 1; i < messages.size(); i++) {
                    // if the message date changes, insert a header message in the arraylist
                    if (!messages.get(i).date.substring(0, 9).equals(messages.get(i - 1).date.substring(0, 9))) {
                        messages.add(i, new MessageDto("header", messages.get(i).date, ""));
                    }
                }
        }

        mAdapter = new MessageAdapter(messages, getContext());
        recyclerView.setAdapter(mAdapter);


//        MessageDto[] messageList = messages.toArray(new MessageDto[messages.size()]);
//
//        // Post messages
//        Call<User> messageCall = new HttpClient<UserService>(getContext()).create(UserService.class).sendMessages(messageList);
//
//        messageCall.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
////                handleUserResponse(response);
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(getActivity(), "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
//                Timber.e(t);
//            }
//        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void startMessageBackgroundWork() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadMessageWorkRequest = new PeriodicWorkRequest
                .Builder(MessageWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "UPLOAD WORK",
                        ExistingPeriodicWorkPolicy.KEEP,
                        uploadMessageWorkRequest
                );
    }


}
