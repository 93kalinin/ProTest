package android.coursework.protest.Creation;

import android.content.Intent;
import android.content.res.Resources;
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

import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

/**
 * Отвечает за создание тестов. Вопросы хранятся в поле questions, где каждому вопросу
 * сопоставляется набор вариантов ответа, каждому из которых в свою очередь сопоставляется
 * верность/ложность. Адаптер здесь мог бы содержать
 */
public class MakeTest extends AppCompatActivity {

    private Map<String, Map<String, Boolean>> questions;
    private GenericRecyclerAdapter<Map<String, Boolean>> adapter;

    private Resources appResources;
    private RecyclerView questionsView;
    private ConstraintLayout rootLayout;
    private Toolbar toolbar;
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;

    final int CREATE_QUESTION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        appResources = getResources();

        rootLayout = findViewById(R.id.create_test_root_layout);
        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.test_description_input);
        questionsView = findViewById(R.id.questions_recycler_view);
        toolbar = findViewById(R.id.test_creation_toolbar);
        setSupportActionBar(toolbar);
        setUpRecyclerView();

        FloatingActionButton fab = findViewById(R.id.finish_question_creation_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_QUESTION_REQUEST_CODE  &&  resultCode == RESULT_OK) {
            String question = data.getExtras().getString("question");
            Map<String, Boolean> answers =
                (Map<String, Boolean>) data.getExtras().getSerializable("answers");
            adapter.addItem(new SimpleEntry<>(question, answers));
        }
    }

    public void createQuestion(View view) {
        int MAX_QUESTIONS_AMOUNT = appResources.getInteger(R.integer.max_questions_amount);
        String TOO_MANY_QUESTIONS = appResources.getString(R.string.too_many_questions);

        if (questions.size() >= MAX_QUESTIONS_AMOUNT) {
            error(TOO_MANY_QUESTIONS + MAX_QUESTIONS_AMOUNT);
            return;
        }
        Intent intent = new Intent(this, MakeQuestion.class);
        startActivityForResult(intent, CREATE_QUESTION_REQUEST_CODE);
    }

    private void setUpRecyclerView() {
        adapter = new GenericRecyclerAdapter<>(rootLayout,
                appResources.getInteger(R.integer.max_questions_amount));
        RecyclerHelper.finishSetup(questionsView, new LinearLayoutManager(this), adapter);
    }

    private boolean invalidInputs() {
        questions = adapter.getItems();
        int MIN_DESCRIPTION_LENGTH = appResources.getInteger(R.integer.min_description_length);
        int MIN_QUESTIONS_AMOUNT = appResources.getInteger(R.integer.min_questions_amount);
        int MIN_TITLE_LENGTH = appResources.getInteger(R.integer.min_title_length);
        String DESCRIPTION_TOO_SHORT = appResources.getString(R.string.description_too_short);
        String TOO_FEW_QUESTIONS = appResources.getString(R.string.too_few_questions);
        String TITLE_TOO_SHORT = appResources.getString(R.string.title_too_short);

        if (questions.size() < MIN_QUESTIONS_AMOUNT)
            return error(TOO_FEW_QUESTIONS + MIN_QUESTIONS_AMOUNT);
        if (titleInput.getText().length() < MIN_TITLE_LENGTH)
            return error(TITLE_TOO_SHORT + MIN_TITLE_LENGTH);
        if (descriptionInput.getText().length() < MIN_DESCRIPTION_LENGTH)
            return error(DESCRIPTION_TOO_SHORT + MIN_DESCRIPTION_LENGTH);
        return false;
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return true;
    }
}
