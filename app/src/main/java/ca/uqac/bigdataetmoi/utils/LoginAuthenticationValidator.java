package ca.uqac.bigdataetmoi.utils;

import android.text.TextUtils;
import android.widget.EditText;

public class LoginAuthenticationValidator {

    private EditText emailEditText;
    private EditText passwordEditText;
    private String email;
    private String password;

    public LoginAuthenticationValidator(EditText emailEditText, EditText passwordEditText) {
        this.emailEditText = emailEditText;
        this.passwordEditText = passwordEditText;
        this.email = emailEditText.getText().toString();
        this.password = passwordEditText.getText().toString();
    }

    public Boolean areLoginCredentialsValid() {
        return ! TextUtils.isEmpty(email) && ! TextUtils.isEmpty(password);
    }

    public void displayCredentialsErrorOnEditText() {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Veuillez entrer une adresse email.");
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Veuillez entrer un mot de passe.");
        } else if (password.length() < 6) {
            passwordEditText.setError("Veuillez entrer un mot de passe d'au moins 6 caractères.");
        }
    }
}
