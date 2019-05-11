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
 * списками, которых полно в интерфейсе приложения на разных экранах. Для удобного отображения,
 * создания и редактирования элементов списка хранит предварительную, легко редактируемую коллекцию,
 * которая при завершении редактирования преобразуется в Map
 *
 * @param <T> - тип объектов, хранимых в структуре данных.
 */
abstract class GenericRecyclerAdapter<T>
extends RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder> {

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
    final List<SimpleEntry<String, T>> collection;
    private final ConstraintLayout rootLayout;
    private SimpleEntry<String, T> lastDeleted;
    private int lastPosition;
    private int LIMIT;

    GenericRecyclerAdapter(ConstraintLayout rootLayout, int limit) {
        collection = new LinkedList<>();
        this.rootLayout = rootLayout;
        LIMIT = limit;
    }
    //TODO:что делать при отсутствии надобности в обработчике?
    /**
     * Позволяет задать обработчик нажатия на элемент прокручиваемого списка.
     * Если такой обработчик не требуется, то при переопределении достаточно
     *
     * @param view - ссылка на графическое представление элемента списка
     * @param adapterPosition - номер данного элемента в списке
     */
    abstract void onClickListener(View view, int adapterPosition);

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
            Snackbar.make(rootLayout, R.string.limit_exceeded_error, Snackbar.LENGTH_SHORT)
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
        LinkedHashMap<String, T> map = new LinkedHashMap<>();
        for (SimpleEntry<String, T> entry : collection)
            map.put(entry.getKey(), entry.getValue());
        return map;
    }
}
