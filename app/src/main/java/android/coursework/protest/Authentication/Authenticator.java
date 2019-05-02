package android.coursework.protest.Authentication;

import android.content.Intent;
import android.coursework.protest.Browse;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Authenticator extends AppCompatActivity {

    private android.coursework.protest.Authentication.UI UI;
    private InputsValidator inputsValidator;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        inputsValidator = new InputsValidator(this);
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


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    public void animateForSignup(View view) { UI.animateForSignup(); }
    public void animateForSignin(View view) { UI.animateForSignin(); }
}