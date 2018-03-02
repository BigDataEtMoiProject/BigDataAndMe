package ca.uqac.bigdataetmoi.utility;

/**
 * Created by Patrick Lapointe on 2018-02-28.
 * But : Avertir lorsque le statut d'une permission à chagé.
 */

public interface PermissionChangedListener {
    void permissionChanged(String permission, boolean granted);
}
