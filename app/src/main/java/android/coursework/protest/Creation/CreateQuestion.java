package android.coursework.protest.Creation;

import android.content.Intent;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.coursework.protest.Creation.Question.Answer;

/**
 * Отвечает за создание вопроса и прикрепленных к нему вариантов ответа (поле answers).
 */
public class CreateQuestion extends AppCompatActivity {

    public final int MIN_QUESTION_LENGTH = 5;
    public final int MIN_ANSWER_LENGTH = 5;
    public final int MIN_ANSWERS_AMOUNT = 2;

    private List<Answer> answers;
    private ConstraintLayout rootLayout;
    private TextInputEditText answerInput;
    private TextInputEditText questionInput;
    private RecyclerView recyclerView;
    private AnswersRecyclerAdapter adapter;
    private Intent activityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            extractAnswers();
            if (!inputsAreValid()) return;
            activityResult.putExtra("question",
                    new Question(questionInput.getText().toString(), answers));
            setResult(RESULT_OK);
            finish();
                });
        rootLayout = findViewById(R.id.create_question_root_layout);
        answerInput = findViewById(R.id.answer_input);
        recyclerView = findViewById(R.id.answers_recycler_view);
        questionInput = findViewById(R.id.question_input);
        adapter = new AnswersRecyclerAdapter(rootLayout);
        activityResult = new Intent();
        setUpRecyclerView();
    }

    public void addAnswer(View view) {
        String answer = answerInput.getText().toString();
        if (answer.length() < MIN_ANSWER_LENGTH) {
            error("Длина ответа должна быть хотя бы " + MIN_ANSWER_LENGTH + " символов");
            return;
        }
        adapter.addItem(answer);
        answerInput.setText("");
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteAnswers(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void extractAnswers() { answers = new ArrayList<>(adapter.getAnswers()); }

    private boolean inputsAreValid() {
        if (questionInput.getText().length() < MIN_QUESTION_LENGTH)
            return error("Вопросы менее " + MIN_QUESTION_LENGTH + " символов длиной недопустимы");
        if (answers.size() < MIN_ANSWERS_AMOUNT)
            return error("Вариантов ответа должно быть по меньшей мере " + MIN_ANSWERS_AMOUNT);

        boolean atLeastOneCorrect = false;
        for (Answer answer : answers)
            if (answer.isCorrect()) atLeastOneCorrect = true;
        if (!atLeastOneCorrect)
            return error("Хотя бы один из вариантов ответов должен быть верным");
        return true;
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return false;
    }
}
