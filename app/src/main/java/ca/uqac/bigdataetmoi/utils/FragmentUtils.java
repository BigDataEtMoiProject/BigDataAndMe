package ca.uqac.bigdataetmoi.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class FragmentUtils {
    public static void replaceFragment(AppCompatActivity context, int viewIdentifier, Fragment fragment) {
        FragmentManager manager = context.getSupportFragmentManager();
        manager.beginTransaction()
                .replace(viewIdentifier, fragment)
                .addToBackStack(null)
                .commit();
    }
}
