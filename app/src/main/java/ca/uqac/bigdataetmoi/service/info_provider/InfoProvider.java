package ca.uqac.bigdataetmoi.service.info_provider;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.DataCollection;

/**
 * Created by Patrick lapointe on 2018-03-13.
 */

public class InfoProvider {

    private List<DataReadyListener> listeners = new ArrayList<>();

    public void addDataReadyListener(DataReadyListener listener) {
        listeners.add(listener);
    }

    public void unregisterDataReadyListener(DataReadyListener listener) { listeners.remove(listener); }

    protected void generateDataReadyEvent(DataCollection data) {
        for (DataReadyListener listener : listeners)
            listener.dataReady(data);
    }
}
