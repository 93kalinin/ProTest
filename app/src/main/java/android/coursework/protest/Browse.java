package android.coursework.protest;

import android.content.res.Resources;
import android.coursework.protest.R;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedList;

public class Browse extends AppCompatActivity {

    private GenericRecyclerAdapter<QueryDocumentSnapshot> testsAdapter;
    private Resources appResources;
    private ConstraintLayout rootLayout;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Toolbar toolbar = findViewById(R.id.test_creation_toolbar);
        setSupportActionBar(toolbar);
        appResources = getResources();
        db = FirebaseFirestore.getInstance();
        rootLayout = findViewById(R.id.browse_tests_root_layout);

        testsAdapter = new GenericRecyclerAdapter(rootLayout) {

            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                String question = collection.get(position).getKey();
                holder.primaryText.setText(question);
            }
        }

        db.collection("tests")
                .whereEqualTo("accessKey",
                    appResources.getString(R.string.public_access_key).hashCode())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        for (QueryDocumentSnapshot document : task.getResult())
                            testsFromDb.add(document);
                    else error("Unable to access the database");
                });

        FloatingActionButton fab = findViewById(R.id.finish_question_creation_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private boolean error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
        return false;
    }
}
