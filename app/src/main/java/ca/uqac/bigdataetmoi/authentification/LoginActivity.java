package ca.uqac.bigdataetmoi.authentification;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import ca.uqac.bigdataetmoi.MainActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.data.services.LoginService;
import ca.uqac.bigdataetmoi.data.services.HttpClient;
import ca.uqac.bigdataetmoi.dto.UserAuthenticationDto;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.AuthenticationValidator;
import ca.uqac.bigdataetmoi.utils.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

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
        AuthenticationValidator loginValidator = new AuthenticationValidator(fieldEmail, fieldPassword);

        if (loginValidator.areLoginCredentialsValid()) {
            sendLoginRequest();
        } else {
            loginValidator.displayCredentialsErrorOnEditText();
        }
    }

    private void sendLoginRequest() {
        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(fieldEmail.getText().toString(), fieldPassword.getText().toString());
        Call<User> loginCall = new HttpClient<LoginService>().create(LoginService.class).login(userAuthenticationDto);
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
