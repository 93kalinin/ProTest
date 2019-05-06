package android.coursework.protest.Creation;

import android.content.Context;
import android.content.Intent;
import android.coursework.protest.Question;
import android.coursework.protest.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateQuestion extends AppCompatActivity {

    public final int MIN_QUESTION_LENGTH = 5;
    public final int MIN_ANSWERS_AMOUNT = 2;

    private ConstraintLayout rootLayout;
    private TextInputEditText answerInput;
    private TextInputEditText questionInput;
    private RecyclerView recyclerView;
    private AnswersRecyclerAdapter adapter;
    private List<Question.Answer> answers;
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
            activityResult.putExtra("answers", (Serializable) answers);
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
        adapter.addItem(answerInput.getText().toString());
        answerInput.setText("");
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteAnswers(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void extractAnswers() { answers = adapter.getAnswers(); }

    private boolean inputsAreValid() {
        if (questionInput.getText().length() < MIN_QUESTION_LENGTH)
            return error("Вопросы менее " + MIN_QUESTION_LENGTH + " символов длиной недопустимы");
        if (answers.size() < MIN_ANSWERS_AMOUNT)
            return error("Вариантов ответа должно быть по меньшей мере " + MIN_ANSWERS_AMOUNT);

        //можно было бы заменить на answers.toStream().noneMatch(Answer::isCorrect). косяк Android
        boolean atLeastOneCorrect = false;
        for (Question.Answer answer : answers)
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
