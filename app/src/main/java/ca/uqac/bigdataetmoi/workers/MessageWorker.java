package ca.uqac.bigdataetmoi.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.events.OnMessageListUploadedEvent;
import ca.uqac.bigdataetmoi.models.Message;
import ca.uqac.bigdataetmoi.models.Photo;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MessageWorker extends Worker {

    public static final int NUMBER_OF_MESSAGE_TO_SAVE_PER_DAY = 5; // replace by c.getCount() to get all messages per day
    public static final int NUMBER_OF_DAYS_TO_START_FROM = 1; // replace by c.getCount() to get all messages per day
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
        Date lastMessageDateTime = getLastMessageDateTime();

        try {
            List<MessageDto> messages = new ArrayList<>();
            MessageDto messageDto;
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = appContext.getContentResolver();
            Cursor c = cr.query(message, null, null, null, null);

            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    Date dateFormat = new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date"))));
                    if (lastMessageDateTime.getTime() < dateFormat.getTime() - 1000) {
                        messageDto = new MessageDto("", "", "");
                        messageDto.message = c.getString(c.getColumnIndexOrThrow("body"));
                        messageDto.phone = c.getString(c.getColumnIndexOrThrow("address"));
                        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateFormat);
                        messageDto.date = date;

                        messages.add(messageDto);
                    }
                    c.moveToNext();
                }
            } else {
                throw new RuntimeException("You have no SMS");
            }
            c.close();

            Collections.reverse(messages);
            MessageDto[] messageList = messages.toArray(new MessageDto[messages.size()]);

            Call<User> messageCall = new HttpClient<UserService>(appContext).create(UserService.class).sendMessages(messageList);

            try {
                Response<User> res = messageCall.execute();
                if (res.isSuccessful() && res.body() != null) {
                    EventBus.getDefault().post(new OnMessageListUploadedEvent(res.body()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Result.success();
        } catch (SecurityException e) {
            Timber.e(e);
            return Result.failure();
        } catch (Exception e) {
            Timber.e(e);
            return Result.failure();
        }
    }

    private Date getLastMessageDateTime(){
        ArrayList<String> currentMessageList = new ArrayList<>();
        Date lastDate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * NUMBER_OF_DAYS_TO_START_FROM)); // to start save sms from yesterday
        try {
            Response<User> response = UserRepository.getUserFromApi(appContext).execute();

            if (response.isSuccessful() && response.body() != null) {

                for (Message message : response.body().messageList) {
                    if (message.date != null) {
                        currentMessageList.add(message.date);
                    }
                }
                if (currentMessageList.size()>0) {
                    try {
                        lastDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(currentMessageList.get(currentMessageList.size() - 1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastDate;
    }
}
