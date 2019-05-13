package android.coursework.protest.Creation;

import android.coursework.protest.R;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

/**
 * Уменьшает количество повторяющегося кода, связанного с организацией работы с прокручиваемыми
 * списками, которых полно в интерфейсе приложения на разных экранах. Для удобного отображения,
 * создания и редактирования элементов списка хранит предварительную, легко редактируемую коллекцию,
 * которая при завершении редактирования преобразуется в Map
 *
 * @param <T> - тип объектов, хранимых в структуре данных.
 */
class GenericRecyclerAdapter<T>
extends RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder> {

    /**
     * Добавляет прокручиваемым спискам возможность удаления элементов свайпом.
     */
    static void attachDeleteOnSwipe(RecyclerView view, LinearLayoutManager manager,
                                    GenericRecyclerAdapter adapter) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                        | ItemTouchHelper.RIGHT) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                        int position = viewHolder.getAdapterPosition();
                        adapter.removeItem(position);
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder holder,
                                          RecyclerView.ViewHolder target)
                    { return false; }
                });
        view.setAdapter(adapter);
        view.setLayoutManager(manager);
        itemTouchHelper.attachToRecyclerView(view);
    }

    /**
     * Шаблон элемента прокручиваемого списка. Прикрепляет к нему обработчик нажатия.
     * Позволяет использовать заново уже созданные элементы прокручиваемого списка, изменяя их
     * содержимое в соответствии с отображаемой коллекцией. Тем самым экономит ресурсы.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView visibleText;

        ViewHolder(View itemView) {
            super(itemView);
            visibleText = itemView.findViewById(R.id.card_text);
            itemView.setOnClickListener(view -> onClickListener(view, getAdapterPosition()));
        }
    }

    /*
        Коллекция в поле collection должна позволять извлекать элементы по их порядковому номеру
        чтобы работать с необходимым для реализации прокручиваемого списка методом onBindViewHolder
        Одновременно, она должна содержать пары ключ-значение, чем и объясняется её сложный тип.
     */
    protected final List<SimpleEntry<String, T>> collection;
    private final ConstraintLayout rootLayout;
    private SimpleEntry<String, T> lastDeleted;
    private int lastPosition;
    int LIMIT = Integer.MAX_VALUE;

    GenericRecyclerAdapter(ConstraintLayout rootLayout, LinkedList<SimpleEntry<String, T>> init) {
        collection = init;
        this.rootLayout = rootLayout;
    }
    /**
     * Переопределив этот метод, можно задать обработчик нажатия на элемент списка
     *
     * @param view - ссылка на графическое представление элемента списка
     * @param adapterPosition - номер данного элемента в списке
     */
    void onClickListener(View view, int adapterPosition) { }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.generic_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder viewHolder, int position) {
        String question = collection.get(position).getKey();
        viewHolder.visibleText.setText(question);
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    void addItem(SimpleEntry<String, T> item) {
        if (collection.size() >= LIMIT) {
            Snackbar.make(rootLayout, R.string.limit_exceeded_error,
                          Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        collection.add(item);
        notifyDataSetChanged();
    }

    void removeItem(int position) {
        lastDeleted = collection.get(position);
        lastPosition = position;
        collection.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(rootLayout, R.string.element_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.cancel, view -> {
                    if (lastDeleted == null  ||  collection.size() >= LIMIT) {
                        Snackbar.make(rootLayout, R.string.failed_to_restore, Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    collection.add(lastPosition, lastDeleted);
                    notifyItemInserted(lastPosition);
                    lastDeleted = null;
                })
                .show();
    }

    Map<String, T> getItems() {
        HashMap<String, T> map = new HashMap<>();
        for (SimpleEntry<String, T> entry : collection)
            map.put(entry.getKey(), entry.getValue());
        return map;
    }
}
