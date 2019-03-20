package ca.uqac.bigdataetmoi.authentification;

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

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.startup.BaseActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private FirebaseAuth auth;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        fieldEmail = findViewById(R.id.register_input_email);
        fieldPassword = findViewById(R.id.register_input_password);
        goToLogin = findViewById((R.id.register_button_login));
        final Button btn_Register = findViewById(R.id.register_button_register);

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String email = fieldEmail.getText().toString(),
                    password = fieldPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    fieldEmail.setError(getString(R.string.error_empty_email));
                } else if (TextUtils.isEmpty(password)) {
                    fieldPassword.setError(getString(R.string.error_empty_password));
                } else if (password.length() < 6) {
                    fieldPassword.setError(getString(R.string.signup_error_password_too_short));
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Création effectuée avec succès.", Toast.LENGTH_SHORT).show();
                                        Log.d("BDEM-SIGNUP", "createUserWithEmail:success");
                                        //redirect to login page
                                        finish();
                                    } else {
                                        Log.w("BDEM-SIGNUP", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signin_error_authentication_failed), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
