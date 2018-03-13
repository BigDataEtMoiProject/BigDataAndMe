package ca.uqac.bigdataetmoi.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

@SuppressWarnings("HardCodedStringLiteral")
public class SignupActivity extends BaseActivity {

    private EditText field_Email, field_Password;
    private Button btn_Register;
    private FirebaseAuth auth;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        field_Email = findViewById(R.id.signup_field_email);
        field_Password = findViewById(R.id.signup_field_password);
        btn_Register = findViewById(R.id.signup_btn_register);
        goToLogin = findViewById((R.id.signup_text_go_to_login));

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email = field_Email.getText().toString().trim();
                String password = field_Password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signup_error_empty_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signup_error_empty_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signup_error_password_too_short), Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "createUserWithEmail:success", Toast.LENGTH_SHORT).show();
                                Log.d("BDEM-SIGNUP", "createUserWithEmail:success");
                                //redirect to login page
                                launchLoginActivity(view);
                            } else {
                                Log.w("BDEM-SIGNUP", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.signup_error_authentication_failed), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
        });
    }

    public void launchLoginActivity(View v) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
}
