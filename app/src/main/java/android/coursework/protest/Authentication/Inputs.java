package android.coursework.protest.Authentication;

import android.coursework.protest.R;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Отвечает за извлечение и проверку введенных данных на экране регистрации/входа.
 * Должен быть синглтоном, но не работает корректно если реализован как синглтон
 * (баг или особенность работы с памятью Android).
 */
final class Inputs {

    private static final String passwordError = "более 8 символов, обязательны строчные " +
            "и заглавные буквы, а также цифры";
    private static final Pattern passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    private static final String emailError = "некорректный адрес электронной почты";
    private static final Pattern emailPattern = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final String nicknameError = "более 3 символов, только латинские " +
            "буквы и пробелы";
    private static final Pattern nicknamePattern = Pattern.compile("^[a-zA-Z ]{3,}$");

    final TextInputLayout signinEmailLayout, signinPasswordLayout, signupEmailLayout,
            signupPasswordLayout, signupNicknameLayout;

    /**
     * Найти ссылки на элементы интерфейса, отвечающие за ввод данных, и закрепить за ними
     * функции, проверяющие содержимое на корректность.
     * @param activity -- ссылка на класс Authenticator
     */
    Inputs(AppCompatActivity activity) {
        signinEmailLayout = activity.findViewById(R.id.signin_email_layout);
        signinPasswordLayout = activity.findViewById(R.id.signin_password_layout);
        signupEmailLayout = activity.findViewById(R.id.signup_email_layout);
        signupPasswordLayout = activity.findViewById(R.id.signup_password_layout);
        signupNicknameLayout = activity.findViewById(R.id.signup_nickname_layout);

        setValidationListener(signinEmailLayout, emailPattern, emailError);
        setValidationListener(signinPasswordLayout, passwordPattern, passwordError);
        setValidationListener(signupEmailLayout, emailPattern, emailError);
        setValidationListener(signupPasswordLayout, passwordPattern, passwordError);
        setValidationListener(signupNicknameLayout, nicknamePattern, nicknameError);
    }

    private void setValidationListener(TextInputLayout layout, Pattern validator, String error) {
        EditText inputField_ = layout.getEditText();
        inputField_.setOnFocusChangeListener((inputField, hasFocus) -> {
            if (!hasFocus && !validator.matcher(getInput(layout)).matches())
                layout.setError(error);
            else layout.setError(null);
        });
    }

    boolean signinInputsOK() {
        String email = getInput(signinEmailLayout);
        String password = getInput(signinPasswordLayout);
        return emailPattern.matcher(email).matches()
                && passwordPattern.matcher(password).matches();
    }

    boolean signupInputsOK() {
        String email = getInput(signupEmailLayout);
        String password = getInput(signupPasswordLayout);
        String nickname = getInput(signupNicknameLayout);
        return emailPattern.matcher(email).matches()
                && passwordPattern.matcher(password).matches()
                && nicknamePattern.matcher(nickname).matches();
    }

    String getInput(TextInputLayout layout)
        { return layout.getEditText().getText().toString(); }
}
