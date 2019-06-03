package ca.uqac.bigdataetmoi.ui.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.dto.UserAuthenticationDto;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.ui.MainActivity;
import ca.uqac.bigdataetmoi.ui.register.RegisterActivity;
import ca.uqac.bigdataetmoi.utils.AuthenticationValidator;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    CircularProgressButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fieldEmail = findViewById(R.id.login_input_email);
        fieldPassword = findViewById(R.id.login_input_password);
        TextView buttonRegister = findViewById(R.id.login_button_register);
        btnLogin = findViewById(R.id.login_button_continue);

        btnLogin.setOnClickListener(this::login);

        buttonRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_from_top, R.anim.stationary);
        });
    }

    public void login(View view) {
        AuthenticationValidator loginValidator = new AuthenticationValidator(fieldEmail, fieldPassword);

        if (loginValidator.areLoginCredentialsValid()) {
            sendLoginRequest();
        } else {
            loginValidator.displayCredentialsErrorOnEditText();
        }
    }

    private void sendLoginRequest() {
        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(fieldEmail.getText().toString(), fieldPassword.getText().toString());
        Call<User> loginCall = new HttpClient<UserService>(LoginActivity.this).create(UserService.class).login(userAuthenticationDto);
        btnLogin.startAnimation();

        loginCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                handleLoginResponse(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleLoginFailure(t);
            }
        });
    }

    private void revertButton() {
        btnLogin.revertAnimation();
        btnLogin.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button)); // Reapply rounded shape
    }

    private void handleLoginResponse(Response<User> response) {
        if (response.isSuccessful()) {
            Prefs.setBoolean(getApplicationContext(), Constants.SHARED_PREFS, Constants.IS_LOGGED, true);
            Prefs.setString(getApplicationContext(), Constants.SHARED_PREFS, Constants.USER_EMAIL, fieldEmail.getText().toString());
            Prefs.setString(getApplicationContext(), Constants.SHARED_PREFS, Constants.USER_PASSWORD, fieldPassword.getText().toString());
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Timber.e(response.toString());
            Toast.makeText(getApplicationContext(), getString(R.string.signin_error_authentication_failed), Toast.LENGTH_LONG).show();
            revertButton();
        }
    }

    private void handleLoginFailure(Throwable t) {
        Toast.makeText(getApplicationContext(), "Erreur lors de la tentative de connexion", Toast.LENGTH_SHORT).show();
        Timber.e(t);
        revertButton();
    }
}
