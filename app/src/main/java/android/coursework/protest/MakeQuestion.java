package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import static android.coursework.protest.MyTest.Question.Answer;
import static android.coursework.protest.MyTest.printError;

public class MakeQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Resources appResources = getResources();
        ConstraintLayout rootLayout = findViewById(R.id.create_question_root_layout);
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
            String possibleAnswer = answerInput.getText().toString().trim();
            if (possibleAnswer.length() < appResources.getInteger(R.integer.min_answer_length))
                printError(rootLayout, appResources.getString(R.string.answer_too_short));
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
        EditText sufficientAmountInput = findViewById(R.id.sufficient_amount);
        final int MIN_ANSWERS_AMOUNT = appResources.getInteger(R.integer.min_answers_amount);
        final int MIN_QUESTION_LENGTH = appResources.getInteger(R.integer.min_question_length);

        findViewById(R.id.finish_question_creation_fab).setOnClickListener(view -> {
            int amountOfCorrectAnswers = 0;
            for (Answer answer : answersAdapter.collection)
                if (answer.isCorrect) amountOfCorrectAnswers++;

            String possibleQuestion = questionInput.getText().toString().trim();
            int sufficientAmountOfCorrectAnswers;
            try { sufficientAmountOfCorrectAnswers =
                    Integer.parseInt(sufficientAmountInput.getText().toString());
            } catch (NumberFormatException e) {
                printError(rootLayout, appResources.getString(R.string.invalid_sufficient_amount));
                return;
            }

            if (amountOfCorrectAnswers == 0)
                printError(rootLayout, appResources.getString(R.string.no_correct_answers_found));
            else if (sufficientAmountOfCorrectAnswers > amountOfCorrectAnswers
                    || sufficientAmountOfCorrectAnswers < 1)
                printError(rootLayout, appResources.getString(R.string.invalid_sufficient_amount));
            else if (answersAdapter.collection.size() < MIN_ANSWERS_AMOUNT)
                printError(rootLayout, appResources.getString(R.string.too_few_answers, MIN_ANSWERS_AMOUNT));
            else if (possibleQuestion.length() < MIN_QUESTION_LENGTH)
                printError(rootLayout, appResources.getString(R.string.question_too_short, MIN_QUESTION_LENGTH));
            else {
                Intent activityResult = new Intent();
                activityResult.putExtra("question",
                    new MyTest.Question(possibleQuestion, new ArrayList<>(answersAdapter.collection),
                        sufficientAmountOfCorrectAnswers));
                setResult(RESULT_OK, activityResult);
                finish();
            }
        });
    }
}
