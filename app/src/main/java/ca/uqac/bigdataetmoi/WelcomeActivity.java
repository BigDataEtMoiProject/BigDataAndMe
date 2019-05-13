package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.startup.MainMenuActivity;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isAlreadyLogged()) {
            navigateToMainPage();
            return;
        }

        if (hasSeenWelcomePage()) {
            navigateToLoginActivity();
            return;
        }

        setContentView(R.layout.activity_welcome);
    }

    public void onContinueClick(View button) {
        navigateToLoginActivity();
    }

    public void navigateToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_from_right, R.anim.stationary);
        Prefs.setBoolean(this, Constants.SHARED_PREFS, Constants.HAS_SEEN_WELCOME_PAGE, true);
        finish();
    }

    public void navigateToMainPage() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private Boolean hasSeenWelcomePage() {
        return Prefs.getBoolean(this, Constants.SHARED_PREFS, Constants.HAS_SEEN_WELCOME_PAGE);
    }

    private Boolean isAlreadyLogged() {
        return Prefs.getBoolean(this, Constants.SHARED_PREFS, Constants.IS_LOGGED);
    }
}
