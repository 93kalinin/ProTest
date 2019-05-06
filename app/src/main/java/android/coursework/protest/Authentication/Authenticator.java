package android.coursework.protest.Authentication;

import android.content.Intent;
import android.coursework.protest.Creation.CreateQuestion;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Является главным классом экрана регистрации/входа и управляется со стороны ОС. Отвечает за
 * осуществление регистрации/входа с помощью Firebase API.
 */
public class Authenticator extends AppCompatActivity {

    private android.coursework.protest.Authentication.UI UI;
    private Inputs inputs;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        inputs = new Inputs(this);
        UI = new UI(this);
        allowAccess(auth.getCurrentUser()); //для отладки!
    }

    public void signIn(View view) {
        if (!inputs.signinInputsOK()) {
            UI.showSnackbar("В одно или несколько полей введены некорректные данные");
            return;
        }
        UI.showLoadingSpinner();
        String email = inputs.getInput(inputs.signinEmailLayout);
        String password = inputs.getInput(inputs.signinPasswordLayout);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (task) -> {
                    if (task.isSuccessful()) allowAccess(auth.getCurrentUser());
                    else    //possible invalid state at this point
                        UI.showSnackbar("Вход не удался");
                });
    }

    public void signUp(View view) {
        if (!inputs.signupInputsOK()) {
            UI.showSnackbar("В одно или несколько полей введены некорректные данные");
            return;
        }
        UI.showLoadingSpinner();
        String email = inputs.getInput(inputs.signupEmailLayout);
        String password = inputs.getInput(inputs.signupPasswordLayout);
        String nickname = inputs.getInput(inputs.signupNicknameLayout);    //need to attach it

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (task) -> {
                        if (task.isSuccessful()) allowAccess(auth.getCurrentUser());
                        else    //possible invalid state at this point
                            UI.showSnackbar("Регистрация не удалась");
                });
        }

    private void allowAccess(FirebaseUser user) {
        if (user == null) throw new SecurityException("no user is currently logged in");
        Intent intent = new Intent(this, CreateQuestion.class);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();
    }

    public void goToSignup(View view) { UI.goToSignup(); }
    public void goToSignin(View view) { UI.goToSignin(); }
}