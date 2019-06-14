package ca.uqac.bigdataetmoi.workers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.events.OnApplicationUploadedEvent;
import ca.uqac.bigdataetmoi.models.Application;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.repositories.UserRepository;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import retrofit2.Call;
import retrofit2.Response;

public class ApplicationWorker extends Worker {

    private Context appContext;

    public ApplicationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.appContext = context;
    }

    private ArrayList<String> getUserApplicationNameList() {
        try {
            Response<User> response = UserRepository.getUserFromApi(appContext).execute();

            if (response.isSuccessful() && response.body() != null) {
                ArrayList<String> applicationNameList = new ArrayList<>();

                for (Application application : response.body().timeOnAppList) {
                    if (application.appName != null) {
                        applicationNameList.add(application.appName);
                    }
                }

                return applicationNameList;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Boolean isUserApp(ApplicationInfo app) {
        return ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) <= 0);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<ApplicationInfo> apps = appContext.getPackageManager().getInstalledApplications(0);
        List<Application> lesNoms = new ArrayList<>();
        String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
        ArrayList<String> userApplicationNameList = getUserApplicationNameList();

        for (ApplicationInfo app : apps) {
            Application myApp = new Application(app.loadLabel(appContext.getPackageManager()).toString(), currentTime);
            if (isUserApp(app)) {
                if (!userApplicationNameList.contains(myApp.appName)) {
                    lesNoms.add(myApp);
                }
            }
        }

        Call<User> applicationCall = new HttpClient<UserService>(appContext).create(UserService.class).sendApplications(lesNoms);

        try {
            Response<User> res = applicationCall.execute();
            if (res.isSuccessful() && res.body() != null) {
                EventBus.getDefault().post(new OnApplicationUploadedEvent(res.body()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success();
    }
}
