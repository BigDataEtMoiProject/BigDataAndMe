package ca.uqac.bigdataetmoi.activity;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.bigdataetmoi.MainActivity;
import ca.uqac.bigdataetmoi.MainApplication;
import ca.uqac.bigdataetmoi.R;

@SuppressWarnings("HardCodedStringLiteral")
public class LoginActivity extends BaseActivity {

    private EditText field_Email, field_Password;
    private Button btn_Login;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        field_Email = findViewById(R.id.signin_field_email);
        field_Password = findViewById(R.id.signin_field_password);
        btn_Login = findViewById(R.id.signin_btn_login);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = field_Email.getText().toString().trim();
                String password = field_Password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signin_error_empty_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signin_error_empty_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.signin_error_wrong_email_or_password), Toast.LENGTH_LONG).show();
                                } else {
                                    MainApplication.user = auth.getCurrentUser();
                                    Log.e("LOGIN", MainApplication.user.getUid());
                                    launchMainActivity();
                                }
                            }
                        });

            }
        });

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "Activation de la mWifiManager", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
    }

    public void launchMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void launchSignupActivity(View v){
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }
}
