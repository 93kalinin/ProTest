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
import android.widget.EditText;
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

public class Browse extends AppCompatActivity {

    FirebaseFirestore database;
    GenericRecyclerAdapter<MyTest> testsAdapter;
    ConstraintLayout rootLayout;
    Resources appResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rootLayout = findViewById(R.id.browse_tests_root_layout);
        appResources = getResources();


        /*
        Загрузить из базы данных тесты и поставить в соответствие каждому тесту его ID
         */
        HashMap<MyTest, String> testsFromDb = new HashMap<>();
        database = FirebaseFirestore.getInstance();
        database.collection("tests")
                .whereEqualTo("isPrivate", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot test : querySnapshot)
                        testsFromDb.put(test.toObject(MyTest.class), test.getId());
                })
                .addOnFailureListener(fail -> error(fail.getMessage()));

        /*
        Определить testsAdapter и testsRecycler, отвечающие за хранение и отображение тестов,
        загруженных из БД
         */
        RecyclerView testsRecycler = findViewById(R.id.tests_recycler);
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
                String id = appResources.getString(R.string.new_test_id, testsFromDb.get(test));

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
        testsAdapter.collection = new LinkedList<>(testsFromDb.keySet());
        testsAdapter.VIEW_LAYOUT = R.layout.test_card;
        testsRecycler.setAdapter(testsAdapter);
        testsRecycler.setLayoutManager(new LinearLayoutManager(this));

        /*
        Прикрепить обработчик нажатия кнопки доступа к тесту по паролю.
         */
        findViewById(R.id.launch_private_test).setOnClickListener(button -> {
            EditText passwordInput = findViewById(R.id.private_test_password);
            int password;
            try { password = Integer.parseInt(passwordInput.getText().toString()); }
            catch (NumberFormatException e) {
                error(appResources.getString(R.string.int_parse_fail));
                return;
            }

            database.collection("tests")
                    .whereEqualTo("isPrivate", true)
                    .whereEqualTo("accessKey", password)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        MyTest testToPass = querySnapshot
                            .getDocuments()
                            .get(0)
                            .toObject(MyTest.class);
                        offerTest(testToPass);
                })
                .addOnFailureListener(fail ->
                    appResources.getString(R.string.failed_to_find_test));
        });
    }

    /*
    Добавить строку поиска тестов вверху экрана и прикрепить к ней обработчик поиска. Строка поиска
    имеет двойное назначение: при наборе текста она ищет совпадения в названиях тестов, а при
    нажатии кнопки "ввод" на виртуальной клавиатуре ищет тест по введенному ID
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_dashboard, menu);
        SearchView testsSearchView = (SearchView) menu.findItem(R.id.tests_search).getActionView();

        testsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {   // поиск по ID
                database.collection("tests")
                        .document(query)
                        .get()
                        .addOnSuccessListener(documentSnapshot ->
                            offerTest(documentSnapshot.toObject(MyTest.class)))
                        .addOnFailureListener(fail ->
                            error(appResources.getString(R.string.failed_to_find_test)));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                testsAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    /*
    Отобразить сообщение внизу экрана с предложением пройти тест
     */
    void offerTest(MyTest test) {
        Snackbar.make(rootLayout, R.string.test_found, Snackbar.LENGTH_LONG)
                .setAction(R.string.pass_test, view -> {
                    Intent testPassIntent = new Intent(getApplication(), PassTest.class);
                    testPassIntent.putExtra("test", test);
                    startActivity(testPassIntent);
                })
                .show();
    }

    private void error(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
