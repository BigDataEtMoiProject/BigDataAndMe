package ca.uqac.bigdataetmoi.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.dto.MessageDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
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
            List<MessageDto> messages = new ArrayList<>();
            MessageDto messageDto;
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = appContext.getContentResolver();
            Cursor c = cr.query(message, null, null, null, null);

            if (c.moveToFirst()) {
                for (int i = 0; i < 5; i++) {

                    messageDto = new MessageDto("", "", "");
                    messageDto.message = c.getString(c.getColumnIndexOrThrow("body"));
                    messageDto.phone = c.getString(c.getColumnIndexOrThrow("address"));
                    Date dateFormat = new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date"))));
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateFormat);
                    messageDto.date = date;

                    messages.add(messageDto);
                    c.moveToNext();
                }
            } else {
                throw new RuntimeException("You have no SMS");
            }
            c.close();

            MessageDto[] messageList = messages.toArray(new MessageDto[messages.size()]);

            Call<User> messageCall = new HttpClient<UserService>(appContext).create(UserService.class).sendMessages(messageList);

            messageCall.execute();

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
