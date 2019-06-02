package android.coursework.protest;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static android.coursework.protest.Question.Answer;

public class PassTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_test);
        ConstraintLayout rootLayout = findViewById(R.id.test_pass_root_layout);
        TabLayout tabLayout = findViewById(R.id.questions_tabs);
        Resources appResources = getResources();
        /*
        Получить тест для прохождения и определить answersRecycler и answersAdapter, отвечающие
        за хранение и отображение вариантов ответа на вопрос
         */
        MyTest test = (MyTest) getIntent().getExtras().getSerializable("test");
        LinkedList<Question> questions = test.getQuestions();
        RecyclerView answersRecycler = findViewById(R.id.answers_recycler);
        GenericRecyclerAdapter<Question.Answer> answersAdapter =
        new GenericRecyclerAdapter<Answer>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                Answer option = collection.get(position);
                holder.text.setText(option.answer);
            }

            @Override
            void onClickListener(View view, int itemPosition) {
                Answer option = collection.get(itemPosition);
                option.isChecked = !option.isChecked;
                view.setBackgroundResource(option.isChecked ?
                        R.drawable.line_for_selected_items : R.drawable.gray_line);
            }
        };
        answersRecycler.setAdapter(answersAdapter);
        answersRecycler.setLayoutManager(new LinearLayoutManager(this));
        /*
        Создать вкладки с вопросами. Задать обработчик выбора вкладок, который будет загружать
        в answersAdapter варианты ответа, относящиеся к выбранному в данный момент вопросу.
         */
        for (int i = questions.size(); i > 0; --i)
            tabLayout.addTab(tabLayout.newTab()
                                      .setText(String.valueOf(i)));

        TextView questionView = findViewById(R.id.question_view);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int questionNumber = tab.getPosition();
                Question selectedQuestion = questions.get(questionNumber);
                answersAdapter.collection = selectedQuestion.answers;
                questionView.setText(selectedQuestion.question);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        /*
        Задать обработчик нажатия кнопки завершения теста, который вычисляет результат, а затем
        отправляет тестировщику (при условии приватности теста) и отображает тестируемому результат.
         */
        long initialRealTime = SystemClock.elapsedRealtime();    // точка отсчета времени начала теста
        final int MILLISECONDS_TO_MINUTES = 60_000;    // для преобразования миллисекунд в минуты
        FloatingActionButton finishTestButton = findViewById(R.id.finish_test);

        finishTestButton.setOnClickListener(button -> {
            int finalScore = 0;
            for (Question question : questions) {
                int amountOfCorrectAnswers = 0;
                for (Answer answer : question.answers)
                    if (answer.isCorrect && answer.isChecked) amountOfCorrectAnswers++;
                if (amountOfCorrectAnswers >= question.sufficient) finalScore++;
            }
            int amountOfQuestions = questions.size();
            long testCompletionRealTime = SystemClock.elapsedRealtime();
            if (test.isPrivate) {
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                int testCompletionPercentage = (int) (finalScore * 100F) / amountOfQuestions;
                long minutesElapsedSinceTestStart =
                    (testCompletionRealTime - initialRealTime) / MILLISECONDS_TO_MINUTES;
                Map<String, String> testResult = new HashMap<String, String>() {{
                    put("testId", test.testId);
                    put("testTitle", test.title);
                    put("testeeId", currentUser.getUid());
                    put("testeeName", currentUser.getDisplayName());
                    put("completionPercentage", String.valueOf(testCompletionPercentage));
                    put("minutesElapsed", String.valueOf(minutesElapsedSinceTestStart));
                }};
                database.collection("results")
                        .document(test.authorId)
                        .set(testResult, SetOptions.merge());
            }
            String resultMessage = test.hideResult ?
                appResources.getString(R.string.test_complete)
                : appResources.getString(R.string.test_passed, finalScore, amountOfQuestions);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.test_result)
                  .setNeutralButton(R.string.ok, (dialog_, id) -> finish())
                  .setMessage(resultMessage)
                  .setCancelable(false)
                  .create()
                  .show();
        });
    }
}
