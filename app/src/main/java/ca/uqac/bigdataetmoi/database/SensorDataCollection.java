package ca.uqac.bigdataetmoi.database;

/**
 * Created by Patrick Lapointe on 2018-02-27.
 * But : Stocker les données des senseurs selon la version 2 de la bd.
 * Le but étant de regrouper l'information de tous les capteurs sous une même classe.
 */

public class SensorDataCollection {
    public boolean isMoving;
    public float luxLevel;
    public float proximityDistance;

    public SensorDataCollection() {}

    public SensorDataCollection(boolean isMoving, float luxLevel, float proximityDistance) {
        this.isMoving = isMoving;
        this.luxLevel = luxLevel;
        this.proximityDistance = proximityDistance;
    }
}
