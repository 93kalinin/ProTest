package android.coursework.protest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.coursework.protest.MyTest.printError;
import static android.coursework.protest.MyTest.Question;
import static android.coursework.protest.MyTest.Question.Answer;
import static android.coursework.protest.Authenticate.UserRole;

public class Browse extends AppCompatActivity {

    FirebaseFirestore database;
    GenericRecyclerAdapter<MyTest> testsAdapter;
    ConstraintLayout rootLayout;
    Resources appResources;
    UserRole userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rootLayout = findViewById(R.id.browse_tests_root_layout);
        appResources = getResources();
        /*
        Определить роль пользователя в приложении и загрузить соответствующий набор тестов.
        Изначально тестируемому доступны публичные тесты (он может загрузить закрытый тест отдельно),
        тестировщику - тесты, автором которых он является, модератору - все тесты
         */
        LinkedList<MyTest> testsFromDb = new LinkedList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();
        database.collection("users")
                .document("roles")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String roleString = documentSnapshot.getString(user.getUid());
                    UserRole role = UserRole.valueOf(roleString);
                    adjustLayoutFor(role);
                    CollectionReference testsInDb = database.collection("tests");
                    Query testsForThisUserRole =
                          (role == UserRole.TESTEE) ? testsInDb.whereEqualTo("isPrivate", false)
                        : (role == UserRole.TESTER) ? testsInDb.whereEqualTo("authorId", user.getUid())
                        : testsInDb;
                    testsForThisUserRole.get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot testSnapshot : querySnapshot) {
                                    MyTest retreivedTest = testSnapshot.toObject(MyTest.class);
                                    retreivedTest.testId = testSnapshot.getId();
                                    testsFromDb.add(retreivedTest);
                                }
                                setUpTestsRecyclerWith(testsFromDb, role);
                            })
                            .addOnFailureListener(fail -> printError(rootLayout, fail.getMessage()));
                });
        /*
        Прикрепить обработчик нажатия кнопки доступа к тесту по паролю.
         */
        findViewById(R.id.launch_private_test).setOnClickListener(button -> {
            EditText passwordInput = findViewById(R.id.private_test_password);
            int password;
            try { password = Integer.parseInt(passwordInput.getText().toString()); }
            catch (NumberFormatException e) {
                printError(rootLayout, appResources.getString(R.string.int_parse_fail));
                return;
            }

            database.collection("tests")
                    .whereEqualTo("isPrivate", true)
                    .whereEqualTo("accessKey", password)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<DocumentSnapshot> tests = querySnapshot.getDocuments();
                        if (tests.isEmpty()) printError(rootLayout, appResources.getString(R.string.test_not_found));
                        else offerTest(tests.get(0));
                })
                .addOnFailureListener(fail ->
                    printError(rootLayout, appResources.getString(R.string.test_by_password_error)));
        });
    }
    /*
    Определить testsAdapter и testsRecycler, отвечающие за хранение и отображение тестов,
    загруженных из БД. Закрепить за элементами списка тестов функционал, соответствующий роли пользователя
    */
    private void setUpTestsRecyclerWith(LinkedList<MyTest> testsFromDb, UserRole role) {
        userRole = role;
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
                String approvalMark = (test.isApproved) ? " ✓" : " ✗";
                holder.title.setText(test.getTitle() + approvalMark);
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
                View.OnClickListener roleDependentListener = (userRole == UserRole.MODERATOR) ?
                          button -> checkTest(selectedTest)
                        : button -> startActivity(testPassIntent);

                passTestButton.setOnClickListener(roleDependentListener);
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
                                offerTest(documentSnapshot);
                            else
                                printError(rootLayout, appResources.getString(R.string.test_not_found));
                        })
                        .addOnFailureListener(fail ->
                                printError(rootLayout, appResources.getString(R.string.test_by_password_error)));
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
    Просмотреть содержимое теста, не проходя его. Функционал модератора.
     */
    private void checkTest(MyTest test) {
        StringBuilder testContents = new StringBuilder();
        testContents.append(appResources.getString(R.string.test_id, test.testId));
        for (Question question : test.questions) {
            testContents.append(appResources.getString(R.string.line_separator, question.question));
            for (Answer answer : question.answers)
                testContents.append(appResources.getString(R.string.line_separator, answer.answer));
        }
        LinearLayout checkTestLayout = findViewById(R.id.check_test_layout);
        TextView testContentsView = findViewById(R.id.test_contents);
        testContentsView.setText(testContents);

        findViewById(R.id.verify_test).setOnClickListener(button -> {
            Map<String, Boolean> testUpdate = new HashMap<>();
            testUpdate.put("isApproved", true);
            test.isApproved = true;
            database.collection("tests")
                    .document(test.testId)
                    .set(testUpdate, SetOptions.merge())
                    .addOnSuccessListener(task ->
                        Toast.makeText(getApplicationContext(), R.string.test_approved, Toast.LENGTH_SHORT)
                             .show());
            checkTestLayout.setVisibility(View.GONE);
            rootLayout.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.remove_test).setOnClickListener(button -> {
            database.collection("tests")
                    .document(test.testId)
                    .delete()
                    .addOnSuccessListener(task ->
                        Toast.makeText(getApplicationContext(), R.string.test_removed, Toast.LENGTH_SHORT)
                             .show());
            checkTestLayout.setVisibility(View.GONE);
            rootLayout.setVisibility(View.VISIBLE);
        });
        rootLayout.setVisibility(View.GONE);
        checkTestLayout.setVisibility(View.VISIBLE);
    }
    /*
    Отобразить сообщение внизу экрана с предложением пройти тест
     */
    void offerTest(DocumentSnapshot testSnapshot) {
        Snackbar.make(rootLayout, R.string.test_found, Snackbar.LENGTH_LONG)
                .setAction(R.string.pass_test, view -> {
                    Intent testPassIntent = new Intent(getApplication(), PassTest.class);
                    MyTest test = testSnapshot.toObject(MyTest.class);
                    test.testId = testSnapshot.getId();
                    testPassIntent.putExtra("test", test);
                    startActivity(testPassIntent);
                })
                .show();
    }
    /*
    Тестировщику и модератору не нужно поле доступа к тесту по паролю. Только тестировщику должна
    быть доступна кнопка создания нового теста
     */
    private void adjustLayoutFor(UserRole role) {
        if (role == UserRole.TESTER  ||  role == UserRole.MODERATOR) {
            findViewById(R.id.private_test_password).setVisibility(View.GONE);
            findViewById(R.id.launch_private_test).setVisibility(View.GONE);
        }
        if (role == UserRole.TESTER) {
            Intent makeTest = new Intent(getApplication(), MakeTest.class);
            findViewById(R.id.make_test_fab).setVisibility(View.VISIBLE);
            findViewById(R.id.make_test_fab).setOnClickListener(fab -> startActivity(makeTest));
        }
    }
}