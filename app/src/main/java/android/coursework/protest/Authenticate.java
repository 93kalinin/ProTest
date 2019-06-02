package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

import static android.coursework.protest.MyTest.printError;

public class Authenticate extends AppCompatActivity {

    View signupView, signinView, welcomeView, loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Resources appResources = getResources();
        ConstraintLayout rootLayout = findViewById(R.id.auth_root_layout);

        signupView = findViewById(R.id.layout_signup);
        signinView = findViewById(R.id.layout_signin);
        welcomeView = findViewById(R.id.layout_welcome);
        loadingView = findViewById(R.id.loading_layout);
        /*
        Определить паттерны, которым должны отвечать вводимые данные. Найти ссылки на элементы
        интерфейса, отвечающие за ввод данных, и закрепить за ними функции, проверяющие данные на
        соответствие паттернам
         */
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                                               Pattern.CASE_INSENSITIVE);
        Pattern nicknamePattern = Pattern.compile("^[a-zA-Z ]{3,}$");
        String passwordError = appResources.getString(R.string.password_error);
        String emailError = appResources.getString(R.string.email_error);
        String nicknameError = appResources.getString(R.string.nickname_error);
        TextInputLayout signinEmailLayout = findViewById(R.id.signin_email_layout);
        TextInputLayout signinPasswordLayout = findViewById(R.id.signin_password_layout);
        TextInputLayout signupEmailLayout = findViewById(R.id.signup_email_layout);
        TextInputLayout signupPasswordLayout = findViewById(R.id.signup_password_layout);
        TextInputLayout signupNicknameLayout = findViewById(R.id.signup_nickname_layout);

        setValidationListener(signinEmailLayout, emailPattern, emailError);
        setValidationListener(signinPasswordLayout, passwordPattern, passwordError);
        setValidationListener(signupEmailLayout, emailPattern, emailError);
        setValidationListener(signupPasswordLayout, passwordPattern, passwordError);
        setValidationListener(signupNicknameLayout, nicknamePattern, nicknameError);
        /*
        Прикрепить обработчики к кнопкам перехода к экрану входа в приложение и регистрации
        */
        findViewById(R.id.go_to_signin_layout).setOnClickListener(button -> switchTo(signinView));
        findViewById(R.id.go_to_signup_layout).setOnClickListener(button -> switchTo(signupView));
        /*
        Прикрепить обработчик к кнопке, отвечающей за вход. Есть анимация с полоской загрузки
         */
        FirebaseAuth auth = FirebaseAuth.getInstance();
        findViewById(R.id.signin_button).setOnClickListener(button -> {
            String email = getInput(signinEmailLayout);
            String password = getInput(signinPasswordLayout);
            if (emailPattern.matcher(email).matches()
                    && passwordPattern.matcher(password).matches()) {
                switchTo(loadingView);
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(task -> allowAccess())
                    .addOnFailureListener(fail -> {
                        switchTo(signinView);
                        printError(rootLayout, appResources.getString(R.string.signin_fail));
                    });
            }
            else printError(rootLayout, appResources.getString(R.string.invalid_inputs));
        });
        /*
        Прикрепить обработчик к кнопке, отвечающей за регистрацию
        */
        findViewById(R.id.signup_button).setOnClickListener(button -> {
            String email = getInput(signupEmailLayout);
            String password = getInput(signupPasswordLayout);
            String nickname = getInput(signupNicknameLayout);
            UserProfileChangeRequest nicknameRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname).build();
            OnFailureListener failureListener = fail -> {
                switchTo(signupView);
                printError(rootLayout, appResources.getString(R.string.signup_fail));
            };

            if (emailPattern.matcher(email).matches()
                    && passwordPattern.matcher(password).matches()
                    && nicknamePattern.matcher(nickname).matches()) {
                switchTo(loadingView);
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(task -> {
                        FirebaseUser user = auth.getCurrentUser();
                        user.updateProfile(nicknameRequest)
                            .addOnSuccessListener(task1 -> allowAccess())
                            .addOnFailureListener(failureListener);
                    })
                    .addOnFailureListener(failureListener);
            }
            else printError(rootLayout, appResources.getString(R.string.invalid_inputs));
        });
    }

    private void setValidationListener(TextInputLayout layout, Pattern validator, String error) {
        EditText inputField = layout.getEditText();
        inputField.setOnFocusChangeListener((field, hasFocus) -> {
            if (!hasFocus && !validator.matcher(getInput(layout)).matches())
                layout.setError(error);
            else layout.setError(null);
        });
    }

    String getInput(TextInputLayout layout)
        { return layout.getEditText().getText().toString().trim(); }

    private void switchTo(View visible) {
        welcomeView.setVisibility(View.GONE);
        signupView.setVisibility(View.GONE);
        signinView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        visible.setVisibility(View.VISIBLE);
    }

    private void allowAccess() {
        Intent intent = new Intent(this, Browse.class);
        startActivity(intent);
        this.finish();
    }
}