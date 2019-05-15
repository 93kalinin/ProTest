package android.coursework.protest.Creation;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.coursework.protest.R;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

/**
 * Отвечает за создание тестов. Вопросы хранятся в поле questions, где каждому вопросу
 * сопоставляется набор вариантов ответа, каждому из которых в свою очередь сопоставляется
 * верность/ложность.
 */
public class MakeTest extends AppCompatActivity {

    private Map<String, Map<String, Boolean>> questions;
    private GenericRecyclerAdapter<SimpleEntry<String, Map<String, Boolean>>> questionsAdapter;
    private GenericRecyclerAdapter<String> tagsAdapter;
    private LinkedList<String> availableTags;
    private LinkedList<String> selectedTags;
    FirebaseUser user;

    private Resources appResources;
    private RecyclerView questionsView;
    private SearchView tagsSearchView;
    private RecyclerView tagsSuggestionsView;
    private ConstraintLayout rootLayout;
    private Switch privacySwitch;
    private Toolbar toolbar;
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;

    final int CREATE_QUESTION_REQUEST_CODE = 1;
    final String DEFAULT_PUBLIC_ACCESS_KEY = "11111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        appResources = getResources();
        questions = new HashMap<>();
        selectedTags = new LinkedList<>();
        availableTags = new LinkedList<>(Arrays.asList(appResources.getStringArray(R.array.tags)));
        user = FirebaseAuth.getInstance().getCurrentUser();

        rootLayout = findViewById(R.id.create_test_root_layout);
        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.test_description_input);
        questionsView = findViewById(R.id.questions_recycler_view);
        tagsSearchView = findViewById(R.id.tags_search);
        tagsSuggestionsView = findViewById(R.id.tags_recycler_view);
        toolbar = findViewById(R.id.test_creation_toolbar);
        privacySwitch = findViewById(R.id.privacy_switch);
        setSupportActionBar(toolbar);
        setUpQuestionsRecycler();
        setUpTagsSearch();

        findViewById(R.id.finish_question_creation_fab).setOnClickListener(view -> {
            if (!inputsOk()) return;

        });

        findViewById(R.id.add_question_button).setOnClickListener(button -> {
            int MAX_QUESTIONS_AMOUNT = appResources.getInteger(R.integer.max_questions_amount);
            String TOO_MANY_QUESTIONS = appResources.getString(R.string.too_many_questions);

            if (questions.size() >= MAX_QUESTIONS_AMOUNT) {
                error(TOO_MANY_QUESTIONS + MAX_QUESTIONS_AMOUNT);
                return;
            }
            Intent intent = new Intent(this, MakeQuestion.class);
            startActivityForResult(intent, CREATE_QUESTION_REQUEST_CODE);
        });

        privacySwitch.setOnCheckedChangeListener((switch_, isChecked) -> {
            if (isChecked) switch_.setText(R.string.privacy_switch_prompt_on);
            else switch_.setText(R.string.privacy_switch_prompt_off);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CREATE_QUESTION_REQUEST_CODE  &&  resultCode == RESULT_OK) {
            String question = intent.getExtras().getString("question");
            Serializable data = intent.getExtras().getSerializable("answers");
            questionsAdapter.addItem(new SimpleEntry<>(question, (Map<String, Boolean>) data));
        }
    }

    private void setUpQuestionsRecycler() {
        questionsAdapter =
                new GenericRecyclerAdapter<SimpleEntry<String, Map<String, Boolean>>>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                String question = collection.get(position).getKey();
                holder.visibleText.setText(question);
            }
        };
        questionsAdapter.ITEMS_LIMIT = appResources.getInteger(R.integer.max_questions_amount);
        questionsAdapter.attachDeleteOnSwipeTo(questionsView, this);
    }

    private void setUpTagsSearch() {
        tagsAdapter = new GenericRecyclerAdapter<String>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position)
                { holder.visibleText.setText(collection.get(position)); }

            @Override
            void onClickListener(View view, int itemPosition) {
                selectedTags.add(collection.get(itemPosition));
                tagsSearchView.clearFocus();
                Toast.makeText(getApplicationContext(), R.string.tag_added, Toast.LENGTH_SHORT)
                        .show();
                }
            };
        tagsAdapter.setListForSearch(availableTags);
        tagsAdapter.setRowView(R.id.simple_row_text, R.layout.simple_row);
        tagsSuggestionsView.setAdapter(tagsAdapter);
        tagsSuggestionsView.setLayoutManager(new LinearLayoutManager(this));
        tagsSearchView.setOnQueryTextFocusChangeListener((view, isInFocus) ->
            tagsSuggestionsView.setVisibility(isInFocus ? View.VISIBLE : View.GONE));

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        tagsSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        tagsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tagsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                tagsAdapter.getFilter().filter(query);
                return false;
            }
        });
        tagsSuggestionsView.bringToFront();
        tagsSuggestionsView.requestLayout();
    }

    private Map<String, Object> buildTest() {
        Map<String, Object> testDbEntry= new HashMap<>();
        testDbEntry.put("questions", questions);
        testDbEntry.put("creationTime", new Timestamp(new Date().getTime()));
        testDbEntry.put("isPrivate", privacySwitch.isChecked());
        testDbEntry.put("accessKey", DEFAULT_PUBLIC_ACCESS_KEY.hashCode());
        testDbEntry.put("title", titleInput.getText().toString());
        testDbEntry.put("description", descriptionInput.getText().toString());
        testDbEntry.put("tags", selectedTags);
        testDbEntry.put("authorNickname", user.getDisplayName());
        testDbEntry.put("authorId", user.getUid());
        return testDbEntry;
    }

    private void loadTestIntoDB() {

    }

    private boolean inputsOk() {
        questions = new HashMap<>();
        for (SimpleEntry<String, Map<String, Boolean>> question : questionsAdapter)
            questions.put(question.getKey(), question.getValue());

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
        return true;
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return false;
    }
}
