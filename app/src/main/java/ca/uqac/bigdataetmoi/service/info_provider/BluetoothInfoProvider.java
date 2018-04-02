package ca.uqac.bigdataetmoi.service.info_provider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.database.DataCollection;
import ca.uqac.bigdataetmoi.utility.PermissionManager;

import static android.Manifest.permission.*;

/**
 * Created by Raph on 21/11/2017.
 *
 * Modifié par Patrick lapointe le 2018-03-13
 */

public class BluetoothInfoProvider extends InfoProvider
{
    private final static int SEARCH_SECONDS = 20;

    private BluetoothAdapter mBTAdapter;
    private BroadcastReceiver mBroadCastReceiver;

    private List<String> mBluetoothSSID;

    public BluetoothInfoProvider(final Context context)
    {
        if(PermissionManager.getInstance().isGranted(BLUETOOTH_ADMIN))
        {
            mBluetoothSSID = new ArrayList<>();
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();

            // Activation du Bluetooth
            if (mBTAdapter != null && !mBTAdapter.isEnabled())
            {
                mBTAdapter.enable();
            }

            // Si le bluetooth est activé, on démarre une découverte
            if (mBTAdapter != null && mBTAdapter.isEnabled())
            {
                mBroadCastReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            mBluetoothSSID.add(device.getName());
                        }
                    }
                };

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                context.registerReceiver(mBroadCastReceiver, filter);
                mBTAdapter.startDiscovery();

                // On cherche durant un nombre de secondes prédéterminées puis on arrête la recherche et on écris le résultat
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBTAdapter.cancelDiscovery();
                        context.unregisterReceiver(mBroadCastReceiver);

                        DataCollection collection = new DataCollection();
                        collection.bluetoothSSID = mBluetoothSSID;
                        generateDataReadyEvent(collection);
                    }
                }, SEARCH_SECONDS * 1000);
            }
        }
    }
}