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

import static android.coursework.protest.MyTest.printError;

public class MakeTest extends AppCompatActivity {

    GenericRecyclerAdapter<MyTest.Question> questionsAdapter;
    final int CREATE_QUESTION_REQUEST_CODE = 1;
    final int DEFAULT_ACCESS_KEY = 12345;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        rootLayout = findViewById(R.id.create_test_root_layout);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Resources appResources = getResources();
        /*
        Определить questionsAdapter и questionsRecycler, отвечающие за хранение и отображение
        вопросов, созданных пользователем.
         */
        RecyclerView questionsRecycler = findViewById(R.id.questions_recycler_view);
        questionsAdapter = new GenericRecyclerAdapter<MyTest.Question>(rootLayout) {
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
        Определить tagsAdapter и tagsRecycler, отвечающие за хранение и
        отображение тегов. Если уже выбранный тег выбрать повторно, он будет удален.
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
                String tag = collection.get(itemPosition);
                String message = (selectedTags.contains(tag)) ?
                    appResources.getString(R.string.tag_removed, tag)
                    : appResources.getString(R.string.tag_added, tag);

                if (selectedTags.contains(tag)) selectedTags.remove(tag);
                else selectedTags.add(tag);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                     .show();
                tagsSearchView.clearFocus();
            }
        };
        tagsAdapter.collection = availableTags;
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
        tagsRecycler.bringToFront();
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
                printError(rootLayout, appResources.getString(R.string.visible_result_for_public_test));
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final int MIN_DESCRIPTION_LENGTH = appResources.getInteger(R.integer.min_description_length);
        final int MIN_QUESTIONS_AMOUNT = appResources.getInteger(R.integer.min_questions_amount);
        final int MIN_TITLE_LENGTH = appResources.getInteger(R.integer.min_title_length);

        findViewById(R.id.finish_question_creation_fab).setOnClickListener(view -> {
            if (questionsAdapter.collection.size() < MIN_QUESTIONS_AMOUNT)
                printError(rootLayout, appResources.getString(R.string.too_few_questions, MIN_QUESTIONS_AMOUNT));
            else if (titleInput.getText().length() < MIN_TITLE_LENGTH)
                printError(rootLayout, appResources.getString(R.string.title_too_short, MIN_TITLE_LENGTH));
            else if (descriptionInput.getText().length() < MIN_DESCRIPTION_LENGTH)
                printError(rootLayout, appResources.getString(R.string.description_too_short, MIN_DESCRIPTION_LENGTH));
            else {
                int newAccessKey = testIsPrivateSwitch.isChecked() ?
                    questionsAdapter.collection.hashCode() : DEFAULT_ACCESS_KEY;
                MyTest newTest = new MyTest() {{
                    questions = new ArrayList<>(questionsAdapter.collection);
                    creationTime = new Date();
                    isPrivate = testIsPrivateSwitch.isChecked();
                    hideResult = hideResultSwitch.isChecked();
                    accessKey = newAccessKey;
                    title = titleInput.getText().toString().trim();
                    description = descriptionInput.getText().toString().trim();
                    tags = selectedTags;
                    authorNickname = currentUser.getDisplayName();
                    authorId = currentUser.getUid();
                }};

                database.collection("tests")
                    .add(newTest)
                    .addOnSuccessListener(documentReference -> {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        String newTestId = documentReference.getId();
                        String dialogMessage = newTest.getIsPrivate() ?
                            appResources.getString(R.string.new_test_id_and_key, newTestId, newAccessKey)
                            : appResources.getString(R.string.new_test_id, newTestId);
                        dialog.setTitle(R.string.test_added)
                              .setNeutralButton(R.string.ok, (dialog_, id) -> dialog_.cancel())
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
                printError(rootLayout, appResources.getString(R.string.too_many_questions));
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
            MyTest.Question question = (MyTest.Question) intent.getExtras().getSerializable("question");
            questionsAdapter.addItem(question);
        }
    }
}
