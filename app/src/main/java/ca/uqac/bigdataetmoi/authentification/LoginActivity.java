package ca.uqac.bigdataetmoi.authentification;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ca.uqac.bigdataetmoi.startup.BaseActivity;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.MainMenuActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        fieldEmail = findViewById(R.id.login_input_email);
        fieldPassword = findViewById(R.id.login_input_password);
        TextView buttonRegister = findViewById(R.id.login_button_register);
        final Button btnLogin = findViewById(R.id.login_button_continue);
        final Context self = this;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = fieldEmail.getText().toString(),
                    password = fieldPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    fieldEmail.setError(getString(R.string.error_empty_email));
                } else if (TextUtils.isEmpty(password)) {
                    fieldPassword.setError(getString(R.string.error_empty_password));
                } else if (password.length() < 6) {
                    fieldPassword.setError(getString(R.string.signup_error_password_too_short));
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, getString(R.string.signin_error_authentication_failed), Toast.LENGTH_LONG).show();
                                    } else {
                                        ActivityFetcherActivity.user = auth.getCurrentUser();
                                        startActivity(new Intent(self, MainMenuActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null && !wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Activation de la Wifi", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
    }
}
