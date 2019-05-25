package ca.uqac.bigdataetmoi.workers;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;

import ca.uqac.bigdataetmoi.data.services.HttpClient;
import ca.uqac.bigdataetmoi.data.services.UserService;
import ca.uqac.bigdataetmoi.dto.CoordinatesDto;
import ca.uqac.bigdataetmoi.models.User;
import retrofit2.Call;
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

            Timber.d("WORKER Coordinates");
            Timber.d(String.valueOf(longitude));
            Timber.d(String.valueOf(latitude));
            Timber.d(String.valueOf(currentTime));

            CoordinatesDto[] coordinatesDtos = { new CoordinatesDto(String.valueOf(longitude), String.valueOf(latitude), currentTime) };
            Call<User> coordinatesCall = new HttpClient<UserService>(appContext.getApplicationContext()).create(UserService.class).sendCoordinates(coordinatesDtos);

            coordinatesCall.execute();

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
