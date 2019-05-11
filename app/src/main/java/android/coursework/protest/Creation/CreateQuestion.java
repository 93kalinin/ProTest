package android.coursework.protest.Creation;

import android.content.Intent;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.io.Serializable;
import java.util.Map;

/**
 * Отвечает за создание вопроса и прикрепленных к нему вариантов ответа (поле answers).
 */
public class CreateQuestion extends AppCompatActivity {

    public final int MIN_QUESTION_LENGTH = 5;
    public final int MIN_ANSWER_LENGTH = 5;
    public final int MIN_ANSWERS_AMOUNT = 2;

    private Map<String, Boolean> answers;
    private AnswersRecyclerAdapter adapter;
    private Intent activityResult;

    private RecyclerView answersView;
    private Toolbar toolbar;
    private ConstraintLayout rootLayout;
    private TextInputEditText answerInput;
    private TextInputEditText questionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        rootLayout = findViewById(R.id.create_question_root_layout);
        answerInput = findViewById(R.id.answer_input);
        answersView = findViewById(R.id.answers_recycler_view);
        questionInput = findViewById(R.id.question_input);
        toolbar = findViewById(R.id.question_creation_toolbar);

        adapter = new AnswersRecyclerAdapter(rootLayout);
        activityResult = new Intent();
        setSupportActionBar(toolbar);
        setUpRecyclerView();

        FloatingActionButton fab = findViewById(R.id.finish_question_creation_fab);
        fab.setOnClickListener(view -> {
            if (invalidInputs()) return;
            activityResult.putExtra("question", questionInput.getText().toString());
            activityResult.putExtra("answers", (Serializable) answers);
            setResult(RESULT_OK);
            finish();
        });
    }

    public void addAnswer(View view) {
        String answer = answerInput.getText().toString();
        if (answer.length() < MIN_ANSWER_LENGTH) {
            error("Длина ответа должна быть хотя бы " + MIN_ANSWER_LENGTH + " символов");
            return;
        }
        adapter.add(answer);
        answerInput.setText("");
    }

    private void setUpRecyclerView() {
        answersView.setAdapter(adapter);
        answersView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                                               | ItemTouchHelper.RIGHT) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    int position = viewHolder.getAdapterPosition();
                    adapter.removeItem(position);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder holder,
                                      @NonNull RecyclerView.ViewHolder target)
                    { return false; }
                });
        itemTouchHelper.attachToRecyclerView(answersView);
    }

    private boolean invalidInputs() {
        answers = adapter.getAnswers();
        if (answers.size() < MIN_ANSWERS_AMOUNT)
            return error("Вариантов ответа должно быть по меньшей мере " + MIN_ANSWERS_AMOUNT);
        if (!answers.containsValue(true))
            return error("Хотя бы один из вариантов ответов должен быть верным");
        if (questionInput.getText().length() < MIN_QUESTION_LENGTH)
            return error("Вопросы менее " + MIN_QUESTION_LENGTH + " символов длиной недопустимы");
        return false;
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
        return true;
    }
}
