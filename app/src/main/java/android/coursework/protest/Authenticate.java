package android.coursework.protest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Authenticate extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final String passwordError = "более 8 символов, обязательны строчные " +
            "и заглавные буквы, а также цифры";
    private static final Pattern passwordPattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    private static final String emailError = "некорректный адрес электронной почты";
    private static final Pattern emailPattern =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);
    private static final String nicknameError = "более 3 символов, только буквы и пробелы";
    private static final Pattern nicknamePattern = Pattern.compile("^[a-zA-Z ]{3,}$");

    private View welcomeView, signupView, signinView;
    private TextInputEditText signinEmailView, signinPasswordView, signupEmailView,
            signupPasswordView, signupNicknameView;
    private TextInputLayout signinEmailLayout, signinPasswordLayout, signupEmailLayout,
            signupPasswordLayout, signupNicknameLayout;
    private int averageAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initializeUi();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, Browse.class));
            this.finish();
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startActivity(new Intent(this, Browse.class));
            }    //TODO: handle fails to authenticate properly
        else if (response == null) { showToast("Authentication cancelled"); }
    }

    public void authenticateUser(View view) {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .build(),
                REQUEST_CODE);
    }
    */

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    private void initializeUi() {
        welcomeView = findViewById(R.id.layout_welcome);
        signupView = findViewById(R.id.layout_signup);
        signinView = findViewById(R.id.layout_signin);
        signinEmailView = findViewById(R.id.signin_email_input);
        signinPasswordView = findViewById(R.id.signin_password_input);
        signupEmailView = findViewById(R.id.signup_email_input);
        signupPasswordView = findViewById(R.id.signup_password_input);
        signupNicknameView = findViewById(R.id.signup_nickname_input);
        signinEmailLayout = findViewById(R.id.signin_email_layout);
        signinPasswordLayout = findViewById(R.id.signin_password_layout);
        signupEmailLayout = findViewById(R.id.signup_email_layout);
        signupPasswordLayout = findViewById(R.id.signup_password_layout);
        signupNicknameLayout = findViewById(R.id.signup_nickname_layout);

        averageAnimationDuration = getResources()
                .getInteger(android.R.integer.config_mediumAnimTime);


        signinPasswordView.setOnFocusChangeListener((inputField, hasFocus) -> {
            if (!hasFocus && passwordIsNotValid(getString((TextInputEditText)inputField))) {
                signinPasswordLayout.setError(passwordError);
            }
            else signinPasswordLayout.setError(null);
        });

        signinEmailView.setOnFocusChangeListener((inputField, hasFocus) -> {
            if (!hasFocus && emailIsNotValid(getString((TextInputEditText)inputField)))
                signinEmailLayout.setError(emailError);
            else signinEmailLayout.setError(null);
        });

        signupPasswordView.setOnFocusChangeListener((inputField, hasFocus) -> {
            if (!hasFocus && passwordIsNotValid(getString((TextInputEditText)inputField)))
                signupPasswordLayout.setError(passwordError);
            else signupPasswordLayout.setError(null);
        });

        signupEmailView.setOnFocusChangeListener((inputField, hasFocus) -> {
            if (!hasFocus && emailIsNotValid(getString((TextInputEditText)inputField)))
                signupEmailLayout.setError(emailError);
            else signupEmailLayout.setError(null);
        });

        signupNicknameView.setOnFocusChangeListener((inputField, hasFocus) -> {
            if (!hasFocus && nicknameIsNotValid(getString((TextInputEditText)inputField)))
                signupNicknameLayout.setError(nicknameError);
            else signupNicknameLayout.setError(null);
        });

        signupView.setVisibility(View.GONE);
        signinView.setVisibility(View.GONE);
    }


    private boolean passwordIsNotValid(String password) {
        return !passwordPattern.matcher(password).matches();
    }

    private boolean emailIsNotValid(String email) {
        return !emailPattern.matcher(email).matches();
    }

    private boolean nicknameIsNotValid(String nick) {
        return !nicknamePattern.matcher(nick).matches();
    }
    private String getString(TextInputEditText inputField) {
        CharSequence input = inputField.getText();
        return (input == null) ? "" : input.toString();
    }

    public void animateForSignup(View view) {
        hideView(welcomeView);
        showView(signupView);
    }

    public void animateForSignin(View view) {
        hideView(welcomeView);
        showView(signinView);
    }

    private void showView(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(averageAnimationDuration);
    }

    private void hideView(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(averageAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }
}