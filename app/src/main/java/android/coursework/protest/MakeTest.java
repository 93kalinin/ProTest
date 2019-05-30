package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class MakeTest extends AppCompatActivity {

    GenericRecyclerAdapter<Question> questionsAdapter;
    final int CREATE_QUESTION_REQUEST_CODE = 1;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        rootLayout = findViewById(R.id.create_test_root_layout);
        Resources appResources = getResources();

        /*
        Определить questionsAdapter и questionsRecycler, отвечающие за хранение и отображение
        вопросов, созданных пользователем.
         */
        RecyclerView questionsRecycler = findViewById(R.id.questions_recycler_view);
        questionsAdapter = new GenericRecyclerAdapter<Question>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                String question = collection.get(position).getQuestion();
                holder.text.setText(question);
            }
        };
        questionsAdapter.ITEMS_LIMIT = appResources.getInteger(R.integer.max_questions_amount);
        questionsAdapter.attachDeleteOnSwipeTo(questionsRecycler);
        questionsRecycler.setAdapter(questionsAdapter);
        questionsRecycler.setLayoutManager(new LinearLayoutManager(this));

        /*
        Определить tagsAdapter и tagsRecycler, отвечающие за хранение и отображение тегов.
        */
        ArrayList<String> selectedTags = new ArrayList<>();
        LinkedList<String> availableTags =
            new LinkedList<>(Arrays.asList(appResources.getStringArray(R.array.tags)));
        SearchView tagsSearchView = findViewById(R.id.tags_search);
        RecyclerView tagsRecycler = findViewById(R.id.tags_recycler_view);
        GenericRecyclerAdapter<String> tagsAdapter =
        new GenericRecyclerAdapter<String>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                String question = collection.get(position);
                holder.text.setText(question);
            }

            @Override
            void onClickListener(View view, int itemPosition) {
                selectedTags.add(collection.get(itemPosition));
                tagsSearchView.clearFocus();
                Toast.makeText(getApplicationContext(), R.string.tag_added, Toast.LENGTH_SHORT)
                        .show();
            }
        };
        tagsAdapter.collection = availableTags;    //TODO: загрузка из бд!
        tagsAdapter.VIEW_LAYOUT = R.layout.simple_row;
        tagsAdapter.TEXT_VIEW_ID = R.id.simple_row_text;
        tagsRecycler.setAdapter(tagsAdapter);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this));

        tagsSearchView.setOnQueryTextFocusChangeListener((view, isInFocus) ->
                tagsRecycler.setVisibility(isInFocus ? View.VISIBLE : View.GONE));
        tagsSearchView.setQueryHint(appResources.getString(R.string.add_tags));
        tagsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
                { return false; }

            @Override
            public boolean onQueryTextChange(String query) {
                tagsAdapter.getFilter().filter(query);
                return false;
            }
        });
        tagsRecycler.bringToFront();    //TODO: necessary?
        tagsRecycler.requestLayout();

        /*
        Прикрепить обработчик нажатия переключателей уровня видимости теста (открытый/закрытый) и
        отображения результата для тестируемого. Обработчик один и тот же, предотвращающий ситуацию,
        при которой у общедоступного теста скрыт результат (это запрещено ТЗ).
        */
        Switch hideResultSwitch = findViewById(R.id.hide_result_switch);
        Switch testIsPrivateSwitch = findViewById(R.id.privacy_switch);
        CompoundButton.OnCheckedChangeListener listener = (switch_, isChecked) -> {
            if (!testIsPrivateSwitch.isChecked() && hideResultSwitch.isChecked()) {
                hideResultSwitch.setChecked(false);
                error(appResources.getString(R.string.visible_result_for_public_test));
            }
        };
        testIsPrivateSwitch.setOnCheckedChangeListener(listener);
        hideResultSwitch.setOnCheckedChangeListener(listener);

        /*
        Прикрепить обработчик нажатия кнопки, отвечающей за завершение создания теста. Обработчик
        проверяет на корректность все данные, относящиеся к созданному пользователем тесту и
        загружает тест в базу данных.
         */
        TextInputEditText titleInput = findViewById(R.id.title_input);
        TextInputEditText descriptionInput = findViewById(R.id.test_description_input);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final int MIN_DESCRIPTION_LENGTH = appResources.getInteger(R.integer.min_description_length);
        final int MIN_QUESTIONS_AMOUNT = appResources.getInteger(R.integer.min_questions_amount);
        final int MIN_TITLE_LENGTH = appResources.getInteger(R.integer.min_title_length);

        findViewById(R.id.finish_question_creation_fab).setOnClickListener(view -> {
            if (questionsAdapter.collection.size() < MIN_QUESTIONS_AMOUNT)
                error(appResources.getString(R.string.too_few_questions, MIN_QUESTIONS_AMOUNT));
            else if (titleInput.getText().length() < MIN_TITLE_LENGTH)
                error(appResources.getString(R.string.title_too_short, MIN_TITLE_LENGTH));
            else if (descriptionInput.getText().length() < MIN_DESCRIPTION_LENGTH)
                error(appResources.getString(R.string.description_too_short, MIN_DESCRIPTION_LENGTH));
            else {
                int accessKey = testIsPrivateSwitch.isChecked() ?
                    questionsAdapter.collection.hashCode()
                    : appResources.getInteger(R.integer.default_access_key);
                MyTest newTest = new MyTest(
                    questionsAdapter.collection,
                    new Date(),
                    testIsPrivateSwitch.isChecked(),
                    hideResultSwitch.isChecked(),
                    accessKey,
                    titleInput.getText().toString(),
                    descriptionInput.getText().toString(),
                    selectedTags,
                    currentUser.getDisplayName(),
                    currentUser.getUid());

                database.collection("tests")
                    .add(newTest)
                    .addOnSuccessListener(documentReference -> {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        String newTestId = documentReference.getId();
                        String dialogMessage = newTest.getIsPrivate() ?
                            appResources.getString(R.string.new_test_id_and_key, newTestId, accessKey)
                            : appResources.getString(R.string.new_test_id, newTest);
                        dialog.setTitle(R.string.test_added)
                            .setMessage(dialogMessage)
                            .setCancelable(true)
                            .create()
                            .show();
                        })
                    .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT)
                            .show());
            }
        });

        /*
        Прикрепить обработчик нажатия кнопки, отвечающей за вызов экрана создания вопроса.
        */
        findViewById(R.id.add_question_button).setOnClickListener(button -> {
            if (questionsAdapter.collection.size()
                    >= appResources.getInteger(R.integer.max_questions_amount))
                error(appResources.getString(R.string.too_many_questions));
            else {
                Intent intent = new Intent(this, MakeQuestion.class);
                startActivityForResult(intent, CREATE_QUESTION_REQUEST_CODE);
            }
        });
    }

    /*
    Определить метод, принимающий возвращаемый экраном создания вопросов вопрос.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CREATE_QUESTION_REQUEST_CODE  &&  resultCode == RESULT_OK) {
            Question question = (Question) intent.getExtras().getSerializable("question");
            questionsAdapter.addItem(question);
        }
    }

    private void error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
