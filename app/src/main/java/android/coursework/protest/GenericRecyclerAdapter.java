package android.coursework.protest;

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
     * В рамках данного приложения в одном элементе может быть два текстовых поля (но не более),
     * потому имеется второй конструктор для этого случая.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView primaryText;
        TextView secondaryText;

        ViewHolder(View itemView, int textViewId) {
            super(itemView);
            primaryText = itemView.findViewById(textViewId);
            itemView.setOnClickListener(view -> onClickListener(view, getAdapterPosition()));
        }

        ViewHolder(View itemView, int primaryTextViewId, int secondaryTextViewId) {
            this(itemView, primaryTextViewId);
            secondaryText = itemView.findViewById(secondaryTextViewId);
        }
    }

    LinkedList<T> collection = new LinkedList<>();
    private LinkedList<String> listForSearch;
    private final ConstraintLayout rootLayout;
    private T lastDeleted;
    private int lastPosition;
    private int TEXT_VIEW_ID = R.id.generic_card_text;
    private int VIEW_LAYOUT = R.layout.generic_card;
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
     * Этот метод потребуется переопределить в случае, если в элементе списка более одного
     * текстового поля
     */
    ViewHolder makeViewHolder(View inflatedView)
        { return new ViewHolder(inflatedView, TEXT_VIEW_ID); }

    /**
     * Переопределив этот метод, можно задать обработчик нажатия на элемент списка. По умолчанию
     * он ничего не делает, игнорируя нажатия
     * @param view - ссылка на графическое представление элемента списка
     * @param adapterPosition - номер данного элемента в списке
     */
    void onClickListener(View view, int adapterPosition) { }

    /**
     * Добавляет прокручиваемым спискам возможность удаления элементов свайпом.
     */
    void attachDeleteOnSwipeTo(RecyclerView view, AppCompatActivity context) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                        | ItemTouchHelper.RIGHT) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i)
                    { removeItem(viewHolder.getAdapterPosition()); }

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
    public Filter getFilter() {
        if (listForSearch == null)
            throw new IllegalStateException("The list for searching in it is not set");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence currentText) {
                if (currentText.length() == 0)
                    return new FilterResults() {{ values = listForSearch; }};
                else {
                    List<String> filteredItems = new LinkedList<>();
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(VIEW_LAYOUT, viewGroup, false);
        return makeViewHolder(view);
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

    void setListForSearch(LinkedList<String> list) { listForSearch = list; }

    public Iterator<T> iterator()
        { return Collections.unmodifiableList(collection).iterator(); }
}
