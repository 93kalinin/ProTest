package android.coursework.protest;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/*
 * Отвечает за просмотр списка доступных для прохождения тестов и поиск.
 * Поле retreivedTests содержит словарь, сопоставляющий тесту его id в FirestoreDB.
 */
public class Browse extends AppCompatActivity {

    private GenericRecyclerAdapter<MyTest> testsAdapter;
    private HashMap<MyTest, String> retreivedTests;
    private RecyclerView testsRecycler;
    private Resources appResources;
    private ConstraintLayout rootLayout;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appResources = getResources();
        testsRecycler = findViewById(R.id.tests_recycler);
        db = FirebaseFirestore.getInstance();
        rootLayout = findViewById(R.id.browse_tests_root_layout);
        retreivedTests = new HashMap<>();

        db.collection("tests")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot test : querySnapshot)
                        retreivedTests.put(test.toObject(MyTest.class), test.getId());
                    setUpTestsRecycler();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_dashboard, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.tests_search).getActionView();
        setUpSerach(searchView);
        return true;
    }

    private void setUpTestsRecycler() {
        testsAdapter = new GenericRecyclerAdapter<MyTest>(rootLayout) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                View inflatedView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(VIEW_LAYOUT, viewGroup, false);
                ViewHolder holder = new ViewHolder(inflatedView);
                holder.title = findViewById(R.id.test_title);
                holder.id = findViewById(R.id.test_id);
                holder.description = findViewById(R.id.test_description);
                holder.tags = findViewById(R.id.test_tags);
                return holder;
            }

            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                MyTest test = collection.get(position);
                String id = appResources.getString(R.string.new_test_id, retreivedTests.get(test));

                holder.title.setText(test.getTitle());
                holder.id.setText(id);
                holder.description.setText(test.getDescription());
                holder.tags.setText(TextUtils.join(" | ", test.getTags()));
            }

            @Override
            void onClickListener(View testCardView, int adapterPosition) {
                TextView testDescription = testCardView.findViewById(R.id.test_description);
                Button passTestButton = testCardView.findViewById(R.id.pass_test_button);
                TextView idTextView = testCardView.findViewById(R.id.test_id);
                MyTest selectedTest = collection.get(adapterPosition);

                int newVisibility = (passTestButton.getVisibility() == View.VISIBLE) ?
                    View.GONE : View.VISIBLE;
                Intent testPassIntent = new Intent(getApplication(), PassTest.class);
                testPassIntent.putExtra("test", selectedTest);

                passTestButton.setOnClickListener(button -> startActivity(testPassIntent));
                testDescription.setVisibility(newVisibility);
                passTestButton.setVisibility(newVisibility);
                idTextView.setVisibility(newVisibility);
            }
        };
        testsAdapter.collection = new LinkedList<>(retreivedTests.keySet());
        testsAdapter.VIEW_LAYOUT = R.layout.test_card;
        testsRecycler.setAdapter(testsAdapter);
        testsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpSerach(SearchView testsSearchView) {
        testsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {   // поиск по ID
                db.collection("tests")
                        .document(query)
                        .get()
                        .addOnCompleteListener(task -> idSearchHandler(task));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                testsAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    /* Нужен для улучшения читаемости кода путем выноса его части в оделный метод */
    private void idSearchHandler(Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            Snackbar.make(rootLayout, R.string.test_found, Snackbar.LENGTH_LONG)
                    .setAction(R.string.pass_test, view -> {
                        MyTest test = task.getResult().toObject(MyTest.class);
                        Intent testPassIntent = new Intent(getApplication(), PassTest.class);
                        testPassIntent.putExtra("test", test);
                        startActivity(testPassIntent);
                    })
                    .show();
        }
        else Snackbar.make(rootLayout, R.string.failed_to_find_test, Snackbar.LENGTH_SHORT)
                .show();
    }
}
