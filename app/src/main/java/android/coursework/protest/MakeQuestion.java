package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

/**
 * Отвечает за создание вопроса и прикрепленных к нему вариантов ответа (поле answers). Каждому
 * варианту ответа сопоставляется его истинность/ложность
 */
public class MakeQuestion extends AppCompatActivity {

    private Map<String, Boolean> answers;
    private GenericRecyclerAdapter<SimpleEntry<String, Boolean>> adapter;
    private Intent activityResult;

    private Resources appResources;
    private RecyclerView answersView;
    private Toolbar toolbar;
    private ConstraintLayout rootLayout;
    private TextInputEditText answerInput;
    private TextInputEditText questionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        appResources = getResources();

        rootLayout = findViewById(R.id.create_question_root_layout);
        answerInput = findViewById(R.id.answer_input);
        answersView = findViewById(R.id.answers_recycler_view);
        questionInput = findViewById(R.id.question_input);
        toolbar = findViewById(R.id.question_creation_toolbar);
        activityResult = new Intent();
        setSupportActionBar(toolbar);
        setUpRecyclerView();

        findViewById(R.id.finish_question_creation_fab).setOnClickListener(view -> {
            if (!inputsOk()) return;
            activityResult.putExtra("question", questionInput.getText().toString());
            activityResult.putExtra("answers", (Serializable) answers);
            setResult(RESULT_OK, activityResult);
            finish();
        });

        findViewById(R.id.add_answer_button).setOnClickListener(button -> {
            String answer = answerInput.getText().toString();
            int MIN_ANSWER_LENGTH = appResources.getInteger(R.integer.min_answer_length);
            String error = appResources.getString(R.string.answer_too_short) + MIN_ANSWER_LENGTH;

            if (answer.length() < MIN_ANSWER_LENGTH) {
                Snackbar.make(rootLayout, error, Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }
            adapter.addItem(new SimpleEntry<>(answer, false));
            answerInput.setText("");
        });
    }

    /*
     * Инициализирует адаптер, организующий работу с прокручиваемым списком вариантов ответа.
     * А именно, цепляет к элементам списка обработчик нажатий, помечающий вариант ответа как
     * верный и отмечающий его в списке цветной полоской. Адаптер содержит пары
     * (вариант ответа : его истинность/ложность), откуда параметр SimpleEntry<String, Boolean>
     */
    private void setUpRecyclerView() {
        adapter = new GenericRecyclerAdapter<SimpleEntry<String, Boolean>>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder,  int position) {
                String question = collection.get(position).getKey();
                TextView view = (TextView) holder.textViews.get(0);
                view.setText(question);
            }

            @Override
            void onClickListener(View view, int itemPosition) {
                SimpleEntry<String, Boolean> answer = collection.get(itemPosition);
                answer.setValue(!answer.getValue());
                if (answer.getValue())
                    view.setBackgroundResource(R.drawable.line_for_selected_items);
                else view.setBackgroundResource(R.drawable.gray_line);
            }
        };
        adapter.ITEMS_LIMIT = appResources.getInteger(R.integer.max_answers_amount);
        adapter.attachDeleteOnSwipeTo(answersView, this);
    }

    private boolean inputsOk() {
        answers = new HashMap<>();
        for (SimpleEntry<String, Boolean> answer : adapter)
            answers.put(answer.getKey(), answer.getValue());

        int MIN_ANSWERS_AMOUNT = appResources.getInteger(R.integer.min_answers_amount);
        int MIN_QUESTION_LENGTH = appResources.getInteger(R.integer.min_question_length);
        String TOO_FEW_ANSWERS = appResources.getString(R.string.too_few_answers);
        String NO_RIGHT_ANSWERS_FOUND = appResources.getString(R.string.no_right_answers_found);
        String QUESTION_TOO_SHORT = appResources.getString(R.string.question_too_short);

        if (answers.size() < MIN_ANSWERS_AMOUNT)
            return error(TOO_FEW_ANSWERS + MIN_ANSWERS_AMOUNT);
        if (!answers.containsValue(true))
            return error(NO_RIGHT_ANSWERS_FOUND);
        if (questionInput.getText().length() < MIN_QUESTION_LENGTH)
            return error(QUESTION_TOO_SHORT + MIN_QUESTION_LENGTH);
        return true;
    }

    boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return false;
    }
}
