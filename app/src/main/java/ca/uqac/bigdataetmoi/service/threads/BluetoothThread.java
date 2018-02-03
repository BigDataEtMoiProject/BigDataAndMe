package ca.uqac.bigdataetmoi.service.threads;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.util.Calendar;

import ca.uqac.bigdataetmoi.database.data_models.BluetoothData;

import static ca.uqac.bigdataetmoi.MainActivity.BTAdapter;

/**
 * Created by Raph on 21/11/2017.
 */

public class BluetoothThread extends Thread implements Runnable {

    private Context context;
    // Broadcast receiver for bluetooth
    public static BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.err.println("On est rentré dans le onReceive ! Action = " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                BluetoothData newDevice = new BluetoothData(Calendar.getInstance().getTime(), device.getName(), device.getAddress());

                System.err.println("Added : " + newDevice.getDeviceName() + newDevice.getAddress());
                Toast t = Toast.makeText(context, newDevice.getDeviceName(), Toast.LENGTH_LONG);
                t.show();

                ca.uqac.bigdataetmoi.service.BigDataService.dbManager.storeSensorData(newDevice);
            }
        }
    };

    public BluetoothThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(bReciever, filter);

        while(true) {
            System.out.println("JE SUIS VIVANT ! ");
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            context.registerReceiver(BigDataService.bReciever, filter);
            System.err.println("Starting discovery ...");
            BTAdapter.startDiscovery();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println("Ending discovery...");
            //context.unregisterReceiver(BigDataService.bReciever);
            BTAdapter.cancelDiscovery();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}