package ca.uqac.bigdataetmoi.ui.keylogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import ca.uqac.bigdataetmoi.R;

public class PopUpKeylogger extends Activity {
    public Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_enable_keylogger);

        mButton = findViewById(R.id.compris);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.95),(int)(height*.58));

        mButton.setOnClickListener(v -> {
            Intent intent2= new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent2, 0);
            finish();
         });
    }


}
