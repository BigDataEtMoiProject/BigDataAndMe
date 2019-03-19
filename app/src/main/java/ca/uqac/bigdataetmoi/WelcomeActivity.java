package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.utils.Constants;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasCheckedNoLongDisplay()) {
            navigateToLoginActivity();
        }

        setContentView(R.layout.activity_welcome);
    }

    public void onContinueClick(View _) {
        navigateToLoginActivity();
    }

    public void navigateToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
        finish();
    }

    public void onNoLongerDisplayThisPageClick(View checkBox) {
        CheckBox _checkBox = (CheckBox) checkBox;

        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(Constants.HAS_CHECKED_NO_DISPLAY, _checkBox.isChecked());
        editor.apply();
    }

    private boolean hasCheckedNoLongDisplay() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.HAS_CHECKED_NO_DISPLAY, false);
    }
}
