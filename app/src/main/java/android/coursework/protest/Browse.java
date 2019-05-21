package android.coursework.protest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedList;
import java.util.List;

public class Browse extends AppCompatActivity {

    private GenericRecyclerAdapter<MyTest> testsAdapter;
    private LinkedList<MyTest> retreivedTests;
    private RecyclerView testsRecycler;
    private Resources appResources;
    private ConstraintLayout rootLayout;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        appResources = getResources();
        testsRecycler = findViewById(R.id.tests_recycler);
        db = FirebaseFirestore.getInstance();
        rootLayout = findViewById(R.id.browse_tests_root_layout);
        retreivedTests = new LinkedList<>();

        db.collection("tests")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot test : querySnapshot)
                        retreivedTests.add(test.toObject(MyTest.class));
                    setUpTestsRecycler();
                });
    }

    private void setUpTestsRecycler() {
        testsAdapter = new GenericRecyclerAdapter<MyTest>(rootLayout) {{
            VIEW_LAYOUT = R.layout.test_card;
            collection = retreivedTests;
            notifyDataSetChanged();
            }

            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                String testTitle = collection.get(position).getTitle();
                List<String> testTags = collection.get(position).getTags();
                holder.primaryText.setText(testTitle);
                holder.secondaryText.setText(TextUtils.join(" | ", testTags));
            }

            @Override
            ViewHolder makeViewHolder(View inflatedView)
                { return new ViewHolder(inflatedView, R.id.test_title, R.id.test_tags); }

            @Override
            void onClickListener(View testCardView, int adapterPosition) {
                TextView testDescription = testCardView.findViewById(R.id.test_description);
                Button passTestButton = testCardView.findViewById(R.id.pass_test_button);
                MyTest selectedTest = collection.get(adapterPosition);

                testDescription.setText(selectedTest.getDescription());
                passTestButton.setOnClickListener(button ->
                    startActivity(new Intent(getApplication(), PassTest.class)));
                testDescription.setVisibility(View.VISIBLE);
                passTestButton.setVisibility(View.VISIBLE);
            }
        };

        testsRecycler.setAdapter(testsAdapter);
        testsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return false;
    }
}
