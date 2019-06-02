package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.List;

public class Browse extends AppCompatActivity {

    FirebaseFirestore database;
    LinkedList<MyTest> testsFromDb;
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
        Загрузить из базы данных общедоступные тесты
         */
        testsFromDb = new LinkedList<>();
        database = FirebaseFirestore.getInstance();
        database.collection("tests")
                .whereEqualTo("isPrivate", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot testSnapshot : querySnapshot) {
                        MyTest retreivedTest = testSnapshot.toObject(MyTest.class);
                        retreivedTest.testId = testSnapshot.getId();
                        testsFromDb.add(retreivedTest);
                    }
                    setUpTestsRecycler();
                })
                .addOnFailureListener(fail -> printError(fail.getMessage()));
        /*
        Прикрепить обработчик нажатия кнопки доступа к тесту по паролю.
         */
        findViewById(R.id.launch_private_test).setOnClickListener(button -> {
            EditText passwordInput = findViewById(R.id.private_test_password);
            int password;
            try { password = Integer.parseInt(passwordInput.getText().toString()); }
            catch (NumberFormatException e) {
                printError(appResources.getString(R.string.int_parse_fail));
                return;
            }

            database.collection("tests")
                    .whereEqualTo("isPrivate", true)
                    .whereEqualTo("accessKey", password)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<DocumentSnapshot> testToPass = querySnapshot
                            .getDocuments();
                        if (testToPass.isEmpty())
                            printError(appResources.getString(R.string.test_not_found));
                        else
                            offerTest(testToPass.get(0).toObject(MyTest.class));
                })
                .addOnFailureListener(fail ->
                    printError(appResources.getString(R.string.test_by_password_error)));
        });
    }
    /*
    Определить testsAdapter и testsRecycler, отвечающие за хранение и отображение тестов,
    загруженных из БД.
    */
    private void setUpTestsRecycler() {
        RecyclerView testsRecycler = findViewById(R.id.tests_recycler);
        testsAdapter = new GenericRecyclerAdapter<MyTest>(rootLayout) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                View inflatedView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(VIEW_LAYOUT, viewGroup, false);
                ViewHolder holder = new ViewHolder(inflatedView);
                holder.title = inflatedView.findViewById(R.id.test_title);
                holder.id = inflatedView.findViewById(R.id.test_id);
                holder.description = inflatedView.findViewById(R.id.test_description);
                holder.tags = inflatedView.findViewById(R.id.test_tags);
                return holder;
            }

            @Override
            public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position) {
                MyTest test = collection.get(position);
                holder.title.setText(test.getTitle());
                holder.id.setText(appResources.getString(R.string.id, test.getTestId()));
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
        testsAdapter.VIEW_LAYOUT = R.layout.test_card;
        testsAdapter.collection = testsFromDb;
        testsRecycler.setAdapter(testsAdapter);
        testsRecycler.setLayoutManager(new LinearLayoutManager(this));
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
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists())
                                offerTest(documentSnapshot.toObject(MyTest.class));
                            else
                                printError(appResources.getString(R.string.test_not_found));
                        })
                        .addOnFailureListener(fail ->
                                printError(appResources.getString(R.string.test_by_password_error)));
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

    private void printError(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
