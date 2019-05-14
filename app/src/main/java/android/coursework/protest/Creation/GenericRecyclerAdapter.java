package android.coursework.protest.Creation;

import android.coursework.protest.R;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Упрощает работу с прокручиваемыми списками типа RecyclerView, подгоняя стандартный класс
 * RecyclerView.Adapter ближе к нуждам моего приложения.
 */
abstract class GenericRecyclerAdapter<T>
extends RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder>
implements Iterable<T>, Filterable {

    /**
     * Шаблон элемента прокручиваемого списка. Прикрепляет к нему обработчик нажатия.
     * Позволяет использовать заново уже созданные элементы прокручиваемого списка, изменяя их
     * содержимое в соответствии с отображаемой коллекцией. Тем самым экономит ресурсы.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView visibleText;

        ViewHolder(View itemView, int textViewId) {
            super(itemView);
            visibleText = itemView.findViewById(textViewId);
            itemView.setOnClickListener(view -> onClickListener(view, getAdapterPosition()));
        }
    }

    LinkedList<T> collection;
    LinkedList<String> listForSearch;
    private final ConstraintLayout rootLayout;
    private T lastDeleted;
    private int lastPosition;
    private int TEXT_VIEW_ID = R.id.simple_row_text;
    private int VIEW_LAYOUT = R.layout.simple_row;
    int ITEMS_LIMIT = Integer.MAX_VALUE;

    GenericRecyclerAdapter(ConstraintLayout rootLayout) { this.rootLayout = rootLayout; }

    /**
     * Этот метод необходимо переопределить для привязки коллекции, хранимой в этом классе к
     * элементам прокручиваемого списка.
     * @param holder - ссылка на элемент
     * @param position - порядковый номер элемента в списке
     */
    @Override
    abstract public void onBindViewHolder(GenericRecyclerAdapter.ViewHolder holder, int position);

    /**
     * Переопределив этот метод, можно задать обработчик нажатия на элемент списка. По умолчанию
     * он ничего не делает, игнорируя нажатия
     * @param view - ссылка на графическое представление элемента списка
     * @param adapterPosition - номер данного элемента в списке
     */
    void onClickListener(View view, int adapterPosition) { }

    public void setListForSearch(LinkedList<String> list) { listForSearch = list; }

    @Override
    public Filter getFilter() {
        if (listForSearch == null)
            throw new IllegalStateException("The list for searching in it is not set");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence currentText) {
                if (currentText.length() == 0)
                    return new FilterResults() {{ values = listForSearch; }};
                else {
                    List<String> filteredItems = new ArrayList<>();
                    for (String item : listForSearch)
                        if (item.toLowerCase().contains(currentText.toString().toLowerCase()))
                            filteredItems.add(item);
                    return new FilterResults() {{ values = filteredItems; }};
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                collection = (LinkedList<T>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Добавляет прокручиваемым спискам возможность удаления элементов свайпом.
     */
    void attachDeleteOnSwipeTo(RecyclerView view, AppCompatActivity context) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                        | ItemTouchHelper.RIGHT) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                        int position = viewHolder.getAdapterPosition();
                        removeItem(position);
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder holder,
                                          RecyclerView.ViewHolder target)
                    { return false; }
                });
        view.setAdapter(this);
        view.setLayoutManager(new LinearLayoutManager(context));
        itemTouchHelper.attachToRecyclerView(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(VIEW_LAYOUT, viewGroup, false);
        return new ViewHolder(view, TEXT_VIEW_ID);
    }

    @Override
    public int getItemCount() { return collection.size(); }

    void addItem(T item) {
        if (collection.size() >= ITEMS_LIMIT) {
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
                    if (lastDeleted == null  ||  collection.size() >= ITEMS_LIMIT) {
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

    void setRowView(int textViewId, int rowViewLayout) {
        TEXT_VIEW_ID = textViewId;
        VIEW_LAYOUT = rowViewLayout;
    }

    public Iterator<T> iterator()
        { return Collections.unmodifiableList(collection).iterator(); }
}
