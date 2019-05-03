package android.coursework.protest.Authentication;

import android.content.Intent;
import android.coursework.protest.Browse;
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
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        inputs = new Inputs(this);
        UI = new UI(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, Browse.class));
            this.finish();
        }
    }

    public void goToSignup(View view) { UI.goToSignup(); }
    public void goToSignin(View view) { UI.goToSignin(); }

    public void signIn(View view) {
        inputs.signinInputsOK();

        }

    public void signUp(View view) {
        if (!inputs.signupInputsOK()) {
            UI.showSnackbar("В одно или несколько полей введены некорректные данные");
            return;
        }
        /*
        UI.showLoadingSpinner();
        String email = inputs.getInput(inputs.signupEmailLayout);
        String password = inputs.getInput(inputs.signupPasswordLayout);
        String nickname = inputs.getInput(inputs.signupNicknameLayout);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (task) -> {
                        if (task.isSuccessful()) user = auth.getCurrentUser();
                        else {
                            UI.showSnackbar("Регистрация не удалась");    //TODO:handle better
                            return;
                        }
                });
        */
        }
}