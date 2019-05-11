package android.coursework.protest.Creation;

import android.coursework.protest.R;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;

/**
 * Уменьшает количество повторяющегося кода, связанного с организацией работы с прокручиваемыми
 * списками, которых полно в интерфейсе приложения на разных экранах. Прокручиваемый список
 * является графическим представлением некоторой структуры данных (поле collection), позволяющим
 * с ней взаимодействовать. Например, нажатие на элемент списка может вызывать
 * функцию (поле onClick), которая будет изменять соответствующий элемент структуры данных.
 * @param <T> - тип объектов, хранимых в структуре данных.
 */
abstract class GenericRecyclerAdapter<T>
extends RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder> {

    /**
     * Шаблон элемента прокручиваемого списка.
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
    private int LIMIT;

    GenericRecyclerAdapter(ConstraintLayout rootLayout, int limit) {
        collection = new LinkedList<>();
        this.rootLayout = rootLayout;
        LIMIT = limit;
    }

    abstract void onClickListener(View view, int adapterPosition);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.generic_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder viewHolder,
                                 int position) {
        String question = collection.get(position).getKey();
        viewHolder.visibleText.setText(question);
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    void addItem(SimpleEntry<String, T> item) {
        if (collection.size() >= LIMIT)
            showSnackbar("Достигнуто предельно допустимое число элементов");
        collection.add(item);
        notifyDataSetChanged();
    }

    void removeItem(int position) {
        lastDeleted = collection.get(position);
        lastPosition = position;
        collection.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    Map<String, T> getItems() {
        LinkedHashMap<String, T> map = new LinkedHashMap<>();
        for (SimpleEntry<String, T> entry : collection)
            map.put(entry.getKey(), entry.getValue());
        return map;
    }

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(rootLayout, "Элемент удален",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("отмена", v -> {
            if (lastDeleted == null  ||  collection.size() >= LIMIT) {
                showSnackbar("Не удалось восстановить элемент");
                return;
            }
            collection.add(lastPosition, lastDeleted);
            notifyItemInserted(lastPosition);
            lastDeleted = null;
            });
        snackbar.show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
