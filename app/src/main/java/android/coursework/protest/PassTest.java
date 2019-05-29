package android.coursework.protest;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

public class PassTest extends AppCompatActivity {

    private MyTest test;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        test = (MyTest) getIntent().getExtras().getSerializable("test");
        toolbar.setTitle(test.getTitle());
        setSupportActionBar(toolbar);
        rootLayout = findViewById(R.id.test_pass_root_layout);

        Map<String, Map<String, Boolean>> questionsAndAnswers = test.getQuestions();
        TabLayout tabLayout = findViewById(R.id.questions_tabs);
        for (int i = 1; i <= questionsAndAnswers.size(); ++i)
            tabLayout.addTab(tabLayout.newTab().setText(((Integer) i).toString()));

        GenericRecyclerAdapter<SimpleEntry<String, Boolean>> answersAdapter =
        new GenericRecyclerAdapter<SimpleEntry<String, Boolean>>(rootLayout) {
            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                SimpleEntry<String, Boolean> answer = collection.get(position);
                TextView view = (TextView) holder.textViews.get(0);
                view.setText(answer.getKey());
            }

            @Override
            void onClickListener(View view, int itemPosition) {
                SimpleEntry<String, Boolean> answer = collection.get(itemPosition);
                answer.setValue(!answer.getValue());
                view.setBackgroundResource(answer.getValue() ?
                        R.drawable.line_for_selected_items : R.drawable.gray_line);
            }
        };

        ArrayList<String> questions = new ArrayList<>(questionsAndAnswers.keySet());
        TextView questionView = findViewById(R.id.question_view);
        RecyclerView answersRecycler = findViewById(R.id.answers_recycler);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int questionNumber = tab.getPosition();
                String question = questions.get(questionNumber);
                Map<String, Boolean> possibleAnswers = questionsAndAnswers.get(question);
                FloatingActionButton fab = findViewById(R.id.next_question);

                for (Map.Entry answer : possibleAnswers.entrySet())
                    answersAdapter.addItem(
                        new SimpleEntry<>((String) answer.getKey(), false));
                questionView.setText(question);

                if (questionNumber == questions.size()) {
                    fab.setBackgroundResource(R.drawable.ic_done_white_24dp);
                    fab.setOnClickListener(view -> {

                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                answersAdapter.clear();
                }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        answersRecycler.setAdapter(answersAdapter);
        answersRecycler.setLayoutManager(new LinearLayoutManager(this));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
