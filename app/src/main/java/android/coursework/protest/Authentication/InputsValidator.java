package android.coursework.protest.Authentication;

import android.coursework.protest.R;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import java.util.regex.Pattern;

/**
 * Отвечает за проверку введенных данных на экране регистрации/входа.
 * Должен быть синглтоном, но не работает корректно если реализован как синглтон
 * (баг или особенность работы с памятью Android). Иммутабелен.
 */
final class InputsValidator {

    private static final String passwordError = "более 8 символов, обязательны строчные " +
            "и заглавные буквы, а также цифры";
    private static final Pattern passwordPattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    private static final String emailError = "некорректный адрес электронной почты";
    private static final Pattern emailPattern =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);
    private static final String nicknameError = "более 3 символов, только латинские " +
            "буквы и пробелы";
    private static final Pattern nicknamePattern = Pattern.compile("^[a-zA-Z ]{3,}$");

    private final TextInputEditText signinEmailView, signinPasswordView, signupEmailView,
            signupPasswordView, signupNicknameView;
    private final TextInputLayout signinEmailLayout, signinPasswordLayout, signupEmailLayout,
            signupPasswordLayout, signupNicknameLayout;

    InputsValidator(AppCompatActivity activity) {
        signinEmailView = activity.findViewById(R.id.signin_email_input);
        signinPasswordView = activity.findViewById(R.id.signin_password_input);
        signupEmailView = activity.findViewById(R.id.signup_email_input);
        signupPasswordView = activity.findViewById(R.id.signup_password_input);
        signupNicknameView = activity.findViewById(R.id.signup_nickname_input);
        signinEmailLayout = activity.findViewById(R.id.signin_email_layout);
        signinPasswordLayout = activity.findViewById(R.id.signin_password_layout);
        signupEmailLayout = activity.findViewById(R.id.signup_email_layout);
        signupPasswordLayout = activity.findViewById(R.id.signup_password_layout);
        signupNicknameLayout = activity.findViewById(R.id.signup_nickname_layout);
        setValidationListeners();
    }



    private void setValidationListeners() {
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
}
