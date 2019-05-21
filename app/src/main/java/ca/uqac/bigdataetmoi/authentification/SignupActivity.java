package ca.uqac.bigdataetmoi.authentification;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.data.services.HttpClient;
import ca.uqac.bigdataetmoi.data.services.UserService;
import ca.uqac.bigdataetmoi.dto.UserAuthenticationDto;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.utils.AuthenticationValidator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SignupActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private CircularProgressButton btnRegister;
    private FirebaseAuth auth;
    private TextView goToLogin;
    private final int EMAIL_ALREADY_USED_CODE = 409;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        fieldEmail = findViewById(R.id.register_input_email);
        fieldPassword = findViewById(R.id.register_input_password);
        goToLogin = findViewById((R.id.register_button_login));
        btnRegister = findViewById(R.id.register_button_register);

        goToLogin.setOnClickListener(view -> finish());
    }

    public void register(View view) {
        AuthenticationValidator registerValidation = new AuthenticationValidator(fieldEmail, fieldPassword);

        if (registerValidation.areLoginCredentialsValid()) {
            sendRegisterRequest();
        } else {
            registerValidation.displayCredentialsErrorOnEditText();
        }
    }

    private void sendRegisterRequest() {
        UserAuthenticationDto userRegisterDto = new UserAuthenticationDto(fieldEmail.getText().toString(), fieldPassword.getText().toString());
        Call<User> registerCall = new HttpClient<UserService>().create(UserService.class).register(userRegisterDto);
        btnRegister.startAnimation();

        registerCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleRegisterResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleRegisterFailure(t);
            }
        });
    }

    private void revertButton() {
        btnRegister.revertAnimation();
        btnRegister.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button)); // Reapply rounded shape
    }

    private void handleRegisterResponse(Response<User> response) {
        if (response.isSuccessful()) {
            btnRegister.stopAnimation();
            Toast.makeText(getApplicationContext(), "Création effectuée avec succès.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            revertButton();
            if (response.code() == EMAIL_ALREADY_USED_CODE) {
                Toast.makeText(getApplicationContext(), "L'adresse e-mail est déjà utilisée par un autre compte.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Une erreur est survenue lors de l'authentification.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleRegisterFailure(Throwable t) {
        Toast.makeText(getApplicationContext(), "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
        Timber.e(t);
        revertButton();
    }
}
