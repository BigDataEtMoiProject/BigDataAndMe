package ca.uqac.bigdataetmoi.service.info_provider;
import ca.uqac.bigdataetmoi.database.DataCollection;

/**
 * Created by Patrick Lapointe on 2018-03-13.
 */

public interface DataReadyListener {
    void dataReady(Object data);
}
