package ca.uqac.bigdataetmoi.authentification;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import ca.uqac.bigdataetmoi.R;

public class SignupActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private CircularProgressButton btnRegister;
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
        btnRegister = findViewById(R.id.register_button_register);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void register(View view) {
        final String email = fieldEmail.getText().toString(),
                password = fieldPassword.getText().toString();
        final Context self = this;

        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError(getString(R.string.error_empty_email));
        } else if (TextUtils.isEmpty(password)) {
            fieldPassword.setError(getString(R.string.error_empty_password));
        } else if (password.length() < 6) {
            fieldPassword.setError(getString(R.string.signup_error_password_too_short));
        } else {
            btnRegister.startAnimation();

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                btnRegister.stopAnimation();
                                Toast.makeText(getApplicationContext(), "Création effectuée avec succès.", Toast.LENGTH_SHORT).show();
                                Log.d("BDEM-SIGNUP", "createUserWithEmail:success");
                                //redirect to login page
                                finish();
                            } else {
                                btnRegister.revertAnimation();
                                btnRegister.setBackground(ContextCompat.getDrawable(self, R.drawable.rounded_button)); // Reapply rounded shape

                                if (task.getException() != null) {
                                    Toast.makeText(getApplicationContext(), "L'adresse e-mail est déjà utilisée par un autre compte.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Une erreur est survenue lors de l'authentification.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
}
