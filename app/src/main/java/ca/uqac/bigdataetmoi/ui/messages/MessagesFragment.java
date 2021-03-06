package ca.uqac.bigdataetmoi.ui.messages;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ca.uqac.bigdataetmoi.events.OnMessageListUploadedEvent;
import ca.uqac.bigdataetmoi.ui.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.Message;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.workers.MessageWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class MessagesFragment extends Fragment {

    public static final int REQUEST_CODE_SMS_PERMISSIONS = 123;
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Message> messages;

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

//        return inflater.inflate(R.layout.fragment_message_permission, container, false);


        if (hasAlreadyAcceptedMessagePermission()) {
            startMessageBackgroundWork();
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_messages, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_message_permission, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addMessagePermissionButtonListener();

        if (hasAlreadyAcceptedMessagePermission()) {
            recyclerView = (RecyclerView) getView().findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        }
        messages = new ArrayList<>();
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
        if (requestCode == REQUEST_CODE_SMS_PERMISSIONS) {
            if (hasGrantedLocationPermission(grantResults)) {
                // Display messages
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

    private void addMessagePermissionButtonListener() {
        Button MessagePermissionButton = getView().findViewById(R.id.message_continue);

        if (MessagePermissionButton != null) {
            MessagePermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForMessagePermission();
                }
            });
        }
    }

    private void askForMessagePermission() {
        requestPermissions(new String[]{Manifest.permission.READ_SMS},
                REQUEST_CODE_SMS_PERMISSIONS);
    }

    private void startMessageBackgroundWork() {
        Constraints workConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadMessageWorkRequest = new PeriodicWorkRequest
                .Builder(MessageWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "MESSAGE WORK",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        uploadMessageWorkRequest
                );
    }

    private void handleUserResponse(Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();

            if (user != null) {
                updateMessages(user);
                Timber.d("§ handleUserResponse");
            }
        } else {
            Timber.e(response.toString());
        }
    }

    public void updateMessages(User user) {
        for (int i = 0; i < user.messageList.size(); i++) {
            Message message = user.messageList.get(i);
            messages.add(message);
        }
        adaptMessageList(messages);

        if (hasAlreadyAcceptedMessagePermission()) {
            mAdapter = new MessageAdapter(messages, getContext());
            recyclerView.setAdapter(mAdapter);
        }
    }

    public void adaptMessageList(List<Message> messages){
        if (messages.size() > 0) {
            Collections.reverse(messages);
            // add fake message to viewHolder (card Recap) : phone = recap, date = lastUpdate, message = messageCount
            messages.add(0, new Message("recap", "", String.valueOf(messages.size())));
            if (messages.size() > 1) {
                messages.add(1, new Message("header", messages.get(1).date, ""));
                for (int i = 2; i < messages.size(); i++) {
                    // if the message date changes, insert a fake message in the arraylist (card Header)
                    if (!messages.get(i).date.substring(0, 9).equals(messages.get(i - 1).date.substring(0, 9))) {
                        messages.add(i, new Message("header", messages.get(i).date, ""));
                    }
                }
            }
        }else{
            // add fake message to viewHolder (card Recap) : phone = recap, date = lastUpdate, message = messageCount
            messages.add(0, new Message("recap", "", String.valueOf(messages.size())));
        }
    }

    private boolean hasAlreadyAcceptedMessagePermission() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasGrantedLocationPermission(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageListUploaded(OnMessageListUploadedEvent event) {
        User user = event.getUser();
        List<Message> messageList = user.messageList;
        adaptMessageList(messageList);
        mAdapter.setmMessages(messageList);
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }
}
