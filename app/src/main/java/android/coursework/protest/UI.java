package android.coursework.protest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.coursework.protest.R;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Отвечает за анимации на экране регистрации/входа.
 * Должен быть синглтоном, но не работает корректно если реализован как синглтон
 * (баг или особенность работы с памятью Android).
 */
final class UI {

    private final View rootLayout, signupView, signinView, welcomeView, loadingView;
    private final int averageAnimationDuration;

    UI(AppCompatActivity activity) {
        rootLayout = activity.findViewById(R.id.root_layout);
        signupView = activity.findViewById(R.id.layout_signup);
        signinView = activity.findViewById(R.id.layout_signin);
        welcomeView = activity.findViewById(R.id.layout_welcome);
        loadingView = activity.findViewById(R.id.loading_layout);
        averageAnimationDuration = activity.getResources()
                .getInteger(android.R.integer.config_mediumAnimTime);
        initializeLayout();
    }

    void goToSignup() {
        hideView(welcomeView);
        showView(signupView);
    }

    void goToSignin() {
        hideView(welcomeView);
        showView(signinView);
    }

    void showLoadingSpinner() {
       signupView.setVisibility(View.GONE);
       signinView.setVisibility(View.GONE);
       loadingView.setVisibility(View.VISIBLE);
    }

    private void initializeLayout() {
        signupView.setVisibility(View.GONE);
        signinView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        welcomeView.setVisibility(View.VISIBLE);
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

    void showSnackbar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
