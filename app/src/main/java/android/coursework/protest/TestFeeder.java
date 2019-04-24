package android.coursework.protest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Предоставить данные для отображеня тестов на экране просмотра списка тестов.
 */
public class TestFeeder extends RecyclerView.Adapter<TestFeeder.TestViewHolder> {

    /**
     * Создать шаблон для отображения отдельного теста из списка. Найти и сохранить ссылки на
     * элементы графического представления шаблона, содержащие данные о тесте
     * (например, название теста или его теги)
     */
    public static class TestViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView tags;

        public TestViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            tags = itemView.findViewById(R.id.tags);
        }
    }

    private MyTest[] tests;

    public TestFeeder(MyTest[] dataset) { tests = dataset; }

    /**
     * Создать графическое представление шаблона теста (layout) и вернуть готовый шаблон.
     */
    @Override
    public TestFeeder.TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_preview, parent, false);
        return new TestViewHolder(layout);
    }

    /**
     * Подставить в шаблон данные, превратив его в законченное графическое представление теста.
     */
    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(tests[position].getTitle());

    }

    @Override
    public int getItemCount() { return tests.length; }
}

