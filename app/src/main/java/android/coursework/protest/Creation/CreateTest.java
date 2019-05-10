package android.coursework.protest.Creation;

import android.content.Intent;
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
import android.coursework.protest.R;

public class CreateTest extends AppCompatActivity {

    final int MIN_DESCRIPTION_LENGTH = 10;
    final int MIN_QUESTIONS_AMOUNT = 3;
    final int MIN_TITLE_LENGTH = 5;
    final int CREATE_QUESTION_REQUEST_CODE = 100;
    private ConstraintLayout rootLayout;
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private RecyclerView recyclerView;
    private QuestionsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.finish_question_creation_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        rootLayout = findViewById(R.id.root_test_creation_layout);
        title_input = findViewById(R.id.title_input);
        recyclerView = findViewById(R.id.questions_recycler_view);
        descriptionInput = findViewById(R.id.test_description_input);
        adapter = new QuestionsRecyclerAdapter(rootLayout);
        setUpRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_QUESTION_REQUEST_CODE  &&  resultCode == RESULT_OK) {
            Question question = (Question) data.getExtras().getSerializable("question");
            adapter.addQuestion(question);
        }
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteQuestions(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private boolean inputsAreValid() {
        if (descriptionInput.getText().length() < MIN_DESCRIPTION_LENGTH)
            return error("Описание менее " + MIN_DESCRIPTION_LENGTH
                    + " символов длиной недопустимо");
        if (adapter.getQuestions().size() < MIN_QUESTIONS_AMOUNT)
            return error("Вопросов должно быть по меньшей мере " + MIN_QUESTIONS_AMOUNT);
        if (titleInput.getText().length() < MIN_TITLE_LENGTH)
            return error("Минимальная длина названия теста - " + MIN_TITLE_LENGTH + " символов");
        return true;
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return false;
    }

    void showSnackbar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
