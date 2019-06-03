package ca.uqac.bigdataetmoi.workers;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;

import ca.uqac.bigdataetmoi.events.OnLocationUploadedEvent;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.dto.CoordinateDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class LocationWorker extends Worker {

    public static final String OUTPUT_KEY = "user";
    private Context appContext;
    private WorkerParameters workerParameters;

    LocationWorker(Context appContext, WorkerParameters workerParameters) {
        super(appContext, workerParameters);
        this.appContext = appContext;
        this.workerParameters = workerParameters;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

            CoordinateDto coordinateDtos = new CoordinateDto(String.valueOf(longitude), String.valueOf(latitude), currentTime);
            Call<User> coordinatesCall = new HttpClient<UserService>(appContext.getApplicationContext()).create(UserService.class).sendCoordinate(coordinateDtos);

            try {
                Response<User> res = coordinatesCall.execute();
                if (res.isSuccessful() && res.body() != null) {
                    EventBus.getDefault().post(new OnLocationUploadedEvent(res.body()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Result.success();
        }
        catch (SecurityException e) {
            Timber.e(e);
            return Result.failure();
        } catch (Exception e) {
            Timber.e(e);
            return Result.failure();
        }
    }
}
