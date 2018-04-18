package ca.uqac.bigdataetmoi.application_usage;

import android.graphics.drawable.Drawable;

/**
 * Created by Joshua on 18/04/2018
 * Classe metier pour l'utilisation des interfaces
 */

public class UsageApp {
    //Nom
    private String name;

    //Package
    private String packageName;

    //Logo
    private Drawable logo;

    //Temps total
    private long timeTotal;

    //Temps d'utilisation moyen
    private long averageTime;

    //Date de dernière utilisation
    private long lastTimeUse;


    //Nombre d'utilisation Total/Dernier mois/Dernière semaine/Aujourd'hui
    private int nbUseTotal;
    private int nbUseLastMonth;
    private int nbUseLastWeek;
    private int nbUseToday;



    public UsageApp(String packageName){
        this.packageName = packageName;
    }

    // Getters/Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public long getLastTimeUse() {
        return lastTimeUse;
    }

    public void setLastTimeUse(long lastTimeUse) {
        this.lastTimeUse = lastTimeUse;
    }

    public int getNbUseTotal() {
        return nbUseTotal;
    }

    public void setNbUseTotal(int nbUseTotal) {
        this.nbUseTotal = nbUseTotal;
    }

    public int getNbUseLastMonth() {
        return nbUseLastMonth;
    }

    public void setNbUseLastMonth(int nbUseLastMonth) {
        this.nbUseLastMonth = nbUseLastMonth;
    }

    public int getNbUseLastWeek() {
        return nbUseLastWeek;
    }

    public void setNbUseLastWeek(int nbUseLastWeek) {
        this.nbUseLastWeek = nbUseLastWeek;
    }

    public int getNbUseToday() {
        return nbUseToday;
    }

    public void setNbUseToday(int nbUseToday) {
        this.nbUseToday = nbUseToday;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getLogo() {
        return logo;
    }

    public void setLogo(Drawable logo) {
        this.logo = logo;
    }

    public long getTimeTotal() {
        return timeTotal;
    }

    public String getTimeTotalInHour(){
        long second = (timeTotal/1000) % 60;
        long minute = (timeTotal/(1000 * 60)) % 60;
        long hour = (timeTotal/(1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void setTimeTotal(long timeTotal) {
        this.timeTotal = timeTotal;
    }
}

