package android.coursework.protest.Creation;

import android.content.Intent;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class CreateTest extends AppCompatActivity {

    final int MAX_QUESTIONS_AMOUNT = 128;
    final int CREATE_QUESTION_REQUEST_CODE = 100;
    private final ArrayList<Question> questions = new ArrayList<>(MAX_QUESTIONS_AMOUNT);

    ConstraintLayout rootLayout = findViewById(R.id.root_test_creation_layout);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_test);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_QUESTION_REQUEST_CODE  &&  requestCode == RESULT_OK) {
            Question question = (Question) data.getExtras().getSerializable("question");
            questions.add(question);
        }
    }


    public void addQuestion(View view) {
        Intent intent= new Intent(this, CreateQuestion.class);
        if (questions.size() < MAX_QUESTIONS_AMOUNT)
            startActivityForResult(intent, CREATE_QUESTION_REQUEST_CODE);
        else showSnackbar("Достигнуто предельно допустимое количество вопросов");
    }

    void showSnackbar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
