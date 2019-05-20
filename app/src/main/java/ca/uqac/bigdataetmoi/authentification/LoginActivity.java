package ca.uqac.bigdataetmoi.authentification;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import ca.uqac.bigdataetmoi.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private FirebaseAuth auth;
    CircularProgressButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        fieldEmail = findViewById(R.id.login_input_email);
        fieldPassword = findViewById(R.id.login_input_password);
        TextView buttonRegister = findViewById(R.id.login_button_register);
        btnLogin = findViewById(R.id.login_button_continue);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                overridePendingTransition(R.anim.slide_from_top, R.anim.stationary);
            }
        });

        final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null && !wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Activation de la Wifi", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
    }

    public void login(View view) {
        final Context self = this;
        final String email = fieldEmail.getText().toString(),
                password = fieldPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError(getString(R.string.error_empty_email));
        } else if (TextUtils.isEmpty(password)) {
            fieldPassword.setError(getString(R.string.error_empty_password));
        } else if (password.length() < 6) {
            fieldPassword.setError(getString(R.string.signup_error_password_too_short));
        } else {
            btnLogin.startAnimation();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                btnLogin.revertAnimation();
                                btnLogin.setBackground(ContextCompat.getDrawable(self, R.drawable.rounded_button)); // Reapply rounded shape
                                Toast.makeText(LoginActivity.this, getString(R.string.signin_error_authentication_failed), Toast.LENGTH_LONG).show();
                            } else {
                                btnLogin.stopAnimation();

                                ActivityFetcherActivity.user = auth.getCurrentUser();

                                Prefs.setBoolean(self, Constants.SHARED_PREFS, Constants.IS_LOGGED, true);

                                startActivity(new Intent(self, MainActivity.class));

                                finish();
                            }
                        }
                    });
        }
    }
}
