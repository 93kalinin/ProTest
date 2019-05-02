package android.coursework.protest.Authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.coursework.protest.R;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Отвечает за анимации на экране регистрации/входа.
 * Должен быть синглтоном, но не работает корректно если реализован как синглтон
 * (баг или особенность работы с памятью Android). Иммутабелен.
 */
final class UI {

    private final View signupView, signinView, welcomeView;
    private final int averageAnimationDuration;

    UI(AppCompatActivity activity) {
        signupView = activity.findViewById(R.id.layout_signup);
        signinView = activity.findViewById(R.id.layout_signin);
        welcomeView = activity.findViewById(R.id.layout_welcome);
        averageAnimationDuration = activity.getResources()
                .getInteger(android.R.integer.config_mediumAnimTime);
        hideSecondaryViews();
    }

    private void hideSecondaryViews() {
        signupView.setVisibility(View.GONE);
        signinView.setVisibility(View.GONE);
    }

    void animateForSignup() {
        hideView(welcomeView);
        showView(signupView);
    }

    void animateForSignin() {
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
