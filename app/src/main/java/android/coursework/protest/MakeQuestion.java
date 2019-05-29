package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.coursework.protest.Question.Answer;

/**
 * Отвечает за создание вопроса и прикрепленных к нему вариантов ответа (поле answers). Каждому
 * варианту ответа сопоставляется его истинность/ложность
 */
public class MakeQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Resources appResources = getResources();

        /*
        Определить answersAdapter и answersRecycler, отвечающие за хранение и отображение
        вариантов ответа, создаваемых пользователем.
         */
        RecyclerView answersRecycler = findViewById(R.id.answers_recycler_view);
        GenericRecyclerAdapter<Answer> answersAdapter =
        new GenericRecyclerAdapter<Answer>(findViewById(R.id.create_question_root_layout)) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder,  int position) {
                String answerInThisPosition = collection.get(position).getAnswer();
                holder.text.setText(answerInThisPosition);
            }

            @Override
            void onClickListener(View view, int itemPosition) {
                Answer selectedAnswer = collection.get(itemPosition);
                selectedAnswer.isCorrect = !selectedAnswer.isCorrect;
                view.setBackgroundResource(selectedAnswer.isCorrect ?
                        R.drawable.line_for_selected_items : R.drawable.gray_line);
            }
        };
        answersAdapter.ITEMS_LIMIT = appResources.getInteger(R.integer.max_answers_amount);
        answersAdapter.attachDeleteOnSwipeTo(answersRecycler);
        answersRecycler.setAdapter(answersAdapter);
        answersRecycler.setLayoutManager(new LinearLayoutManager(this));

        /*
        Прикрепить обработчик нажатия кнопки, отвечающей за добавление варианта ответа на вопрос.
         */
        TextInputEditText answerInput = findViewById(R.id.answer_input);
        findViewById(R.id.add_answer_button).setOnClickListener(button -> {
            String possibleAnswer = answerInput.getText().toString();
            if (possibleAnswer.length() < appResources.getInteger(R.integer.min_answer_length))
                error(appResources.getString(R.string.answer_too_short));
            else {
                answersAdapter.addItem(new Answer(possibleAnswer, false));
                answerInput.setText("");
            }
        });

        /*
        Прикрепить обработчик нажатия кнопки, отвечающей за завершение создания вопроса. Обработчик
        проверяет на корректность все данные, относящиеся к созданному пользователем вопросу и
        возвращает вопрос Question в качестве результата работы данного экрана.
         */
        TextInputEditText questionInput = findViewById(R.id.question_input);
        final int MIN_ANSWERS_AMOUNT = appResources.getInteger(R.integer.min_answers_amount);
        final int MIN_QUESTION_LENGTH = appResources.getInteger(R.integer.min_question_length);
        findViewById(R.id.finish_question_creation_fab).setOnClickListener(view -> {
            boolean atLeastOneAnswerIsCorrect = false;
            for (Answer answer : answersAdapter.collection)
                if (answer.isCorrect) atLeastOneAnswerIsCorrect = true;

            if (!atLeastOneAnswerIsCorrect)
                error(appResources.getString(R.string.no_correct_answers_found));
            else if (answersAdapter.collection.size() < MIN_ANSWERS_AMOUNT)
                error(appResources.getString(R.string.too_few_answers));
            else if (questionInput.getText().length() < MIN_QUESTION_LENGTH)
                error(appResources.getString(R.string.question_too_short));
            else {
                Intent activityResult = new Intent();
                activityResult.putExtra("question", questionInput.getText().toString());
                activityResult.putExtra("answers", answersAdapter.collection);
                setResult(RESULT_OK, activityResult);
                finish();
            }
        });
    }

    void error(String message) {
        Snackbar.make(findViewById(R.id.create_question_root_layout), message, Snackbar.LENGTH_LONG)
                .show();
    }
}
