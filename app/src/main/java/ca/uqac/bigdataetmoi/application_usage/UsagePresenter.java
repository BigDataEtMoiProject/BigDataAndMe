package ca.uqac.bigdataetmoi.application_usage;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.uqac.bigdataetmoi.database.data.UsageAppData;

/**
 * Created by Joshua on 18/04/2018
 * recuperation des données d'utilisation des applications du telephone
 */

@RequiresApi(21)
public class UsagePresenter {
    private UsageStatsManager statsManager;
    private PackageManager pm;
    private List<UsageAppData> usageApps;
    private List<UsageAppData> mostUseApps;

    public UsagePresenter(UsageStatsManager statsManager, PackageManager pm) {
        this.statsManager = statsManager;
        this.pm = pm;

        getUsageApps();
        completeInfoUsageApps();
        getMostUseApps();

    }


    public void getUsageApps(){
        //Période sur un an
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        //Récupérer les UsageStats sur un an
        final List<UsageStats> queryUsageStats = new ArrayList(statsManager.queryAndAggregateUsageStats(cal.getTimeInMillis(), System.currentTimeMillis()).values());
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
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

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
                            usage.setName(pm.getApplicationLabel(app).toString());
                            usage.setLogo(pm.getApplicationLogo(app.packageName));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }

        //Triage
        usageApps = sort(usageApps);
    }

    public void getMostUseApps(){
        mostUseApps = usageApps.subList(usageApps.size() - 3, usageApps.size());

        for(UsageAppData usageApp : usageApps){
            if(usageApp.getName() == null){
                usageApp.setName(usageApp.getPackageName());
            }
            Log.d("usageApps", String.format("App : %s, Time total : %s", usageApp.getName(), usageApp.getTimeTotalInHour()));
        }
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

