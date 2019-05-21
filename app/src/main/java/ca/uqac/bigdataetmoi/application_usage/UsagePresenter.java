package ca.uqac.bigdataetmoi.application_usage;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.UsageAppData;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;

/**
 * Created by Joshua on 18/04/2018
 * recuperation des données d'utilisation des applications du telephone
 */

@RequiresApi(21)
public class UsagePresenter implements IUsageContract.Presenter {
    private UsageStatsManager statsManager;
    private PackageManager packageManager;
    private List<UsageAppData> usageApps;
    private List<UsageAppData> mostUseApps;

    private IUsageContract.View view;

    public  UsagePresenter(@NonNull IUsageContract.View view) {
        if(view != null) {
            this.view = view;
            view.setPresenter(this);
        }
        packageManager = ActivityFetcherActivity.getCurrentActivity().getPackageManager();
        statsManager = (UsageStatsManager) ActivityFetcherActivity.getCurrentActivity().getSystemService(Context.USAGE_STATS_SERVICE);
}

    @Override
    public void start() {
        getUsageApps();

        if(usageApps.size() > 0)
            completeInfoUsageApps();
    }

    public void getUsageApps() {
        //Période sur un an
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -1);

        //Récupérer les UsageStats sur un an
        long beginMillis = cal.getTimeInMillis();
        long endMillis = System.currentTimeMillis();
        final List<UsageStats> queryUsageStats = new ArrayList<>(statsManager.queryAndAggregateUsageStats(beginMillis, endMillis).values());
        usageApps = new ArrayList<>();

        for (UsageStats usage : queryUsageStats) {
            UsageAppData tmpUsageApp = new UsageAppData(usage.getPackageName());
            tmpUsageApp.setLastTimeUse(usage.getLastTimeUsed());
            tmpUsageApp.setTimeTotal(usage.getTotalTimeInForeground());
            usageApps.add(tmpUsageApp);
        }
    }

    public void completeInfoUsageApps(){
        //Récupérer la liste des informations sur les applications installées
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        List<UsageAppData> usageResult = new ArrayList<>();

        for (ApplicationInfo app : apps) {

            //Si c'est une application système, on passe
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            //Sinon c'est une application de l'utilisateur
            else {
                for (UsageAppData usage : usageApps) {
                    if (usage.getPackageName().equals(app.packageName)) {
                        try {
                            usage.setName(packageManager.getApplicationLabel(app).toString());
                            usage.setLogo(packageManager.getApplicationLogo(app.packageName));
                            usageResult.add(usage);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }

        //Triage
        usageApps = sort(usageApps);

        // Envoie du résultat
        view.displayLastWeekUsage(usageResult);
    }


    public List<UsageAppData> sort(List<UsageAppData> list){
        List<UsageAppData> tmpList = list;

        UsageAppData tmp;
        int i, j;
        for(i = 1; i < tmpList.size(); i++){
            tmp = tmpList.get(i);

            for(j = i; j > 0 && tmpList.get(j - 1).getTimeTotal() > tmp.getTimeTotal(); j--){
                tmpList.remove(j);
                tmpList.add(j, tmpList.get(j - 1));
            }
            tmpList.remove(j);
            tmpList.add(j, tmp);
        }

        return tmpList;
    }


}

