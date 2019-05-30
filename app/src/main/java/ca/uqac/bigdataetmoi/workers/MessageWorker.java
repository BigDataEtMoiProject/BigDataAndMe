package ca.uqac.bigdataetmoi.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ca.uqac.bigdataetmoi.data.services.HttpClient;
import ca.uqac.bigdataetmoi.data.services.UserService;
import ca.uqac.bigdataetmoi.dto.CoordinateDto;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.models.Message;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MessageWorker extends Worker {

    public static final String OUTPUT_KEY = "user";
    private Context appContext;
    private WorkerParameters workerParameters;

    public MessageWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        this.appContext = appContext;
        this.workerParameters = workerParameters;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Timber.d("************ WORKER Messages ************");
            // messages retrieved from contentResolver
//            List<MessageDto> messages = new ArrayList<>();
//            MessageDto messageDto;
//            Uri message = Uri.parse("content://sms/");
//            ContentResolver cr = appContext.getContentResolver();
//            Cursor c = cr.query(message, null, null, null, null);
////            int totalSMS = c.getCount();
//
//            if (c.moveToFirst()) {
//                for (int i = 0; i < 5; i++) {
//
//                    messageDto = new MessageDto("", "", "");
//                    messageDto.message = c.getString(c.getColumnIndexOrThrow("body"));
////                        messageDto.setReadState(c.getString(c.getColumnIndex("read")));
//                    messageDto.phone = c.getString(c.getColumnIndexOrThrow("address"));
//                    Date dateFormat= new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date"))));
//                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateFormat);
//                    messageDto.date = date;
//
//                    messages.add(messageDto);
//                    c.moveToNext();
//                }
//            }
////             else {
////             throw new RuntimeException("You have no SMS");
////             }
//            c.close();


            // messages test
            String currentDateandTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            List<MessageDto> messages = new ArrayList<>();
            messages.add(new MessageDto("0643463466", currentDateandTime, "youhouuuuuuuuuuuuuuuuuu"));
            messages.add(new MessageDto("0643463466", currentDateandTime, "youhouuuuuuuuuuuuuuuuuu"));
            messages.add(new MessageDto("0569696969", currentDateandTime, "heyyyyyyyyyyyyyyyyyyy"));

            // to see messages
            for (MessageDto mess : messages) {
                Timber.d("**********" + mess.phone + " - " + mess.date + " - " + mess.message);
            }

//            // Post messages
//            MessageDto[] messageList = messages.toArray(new MessageDto[messages.size()]);
//
//            Call<User> messageCall = new HttpClient<UserService>(appContext).create(UserService.class).sendMessages(messageList);
//
//            messageCall.enqueue(new Callback<User>() {
//                @Override
//                public void onResponse(Call<User> call, Response<User> response) {
////                handleUserResponse(response);
//                }
//                @Override
//                public void onFailure(Call<User> call, Throwable t) {
//                    Toast.makeText(appContext, "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
//                    Timber.e(t);
//                }
//            });

            return Result.success();
        } catch (SecurityException e) {
            Timber.e(e);
            return Result.failure();
        } catch (Exception e) {
            Timber.e(e);
            return Result.failure();
        }
    }
}
