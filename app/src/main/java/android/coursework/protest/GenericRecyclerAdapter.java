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
     * Может содержать несколько текстовых полей
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        ArrayList<TextView> textViews = new ArrayList<>();

        ViewHolder(View itemView, int... textViewIds) {
            super(itemView);
            for (int id : textViewIds)
                textViews.add(itemView.findViewById(id));
            itemView.setOnClickListener(view -> onClickListener(view, getAdapterPosition()));
        }
    }

    LinkedList<T> collection = new LinkedList<>();
    private LinkedList<T> backup;
    private boolean originalBackup = true;
    private final ConstraintLayout rootLayout;
    private T lastDeleted;
    private int lastPosition;
    int TEXT_VIEW_ID = R.id.generic_card_text;    // id текстового поля элемента по умолчанию
    int VIEW_LAYOUT = R.layout.generic_card;    // внешний вид элемента списка по умолчанию
    int ITEMS_LIMIT = Integer.MAX_VALUE;    // предельно допустимое число элементов по умолчанию

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


    /**
     * Этот метод потребуется переопределить в случае, если в элементе списка более одного
     * текстового поля. Id этих полей нужно будет передать конструктору ViewHolder для регистрации
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflatedView = LayoutInflater.from(viewGroup.getContext())
                .inflate(VIEW_LAYOUT, viewGroup, false);
        return new ViewHolder(inflatedView, TEXT_VIEW_ID);
    }

    /*
     * Отвечает за реализацию поиска. Подменяет ссылку на коллекцию всех элементов списка
     * ссылкой на коллекцию только тех элементов, которые удовлетворяют критериям поиска.
     * Для того, чтобы ссылка на исходную коллекцию не была при этом утрачена, использует метод
     * setCollectionState.
     */
    @Override
    public Filter getFilter() {
        setCollectionState();
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence currentText) {
                if (currentText.length() == 0)
                    return new FilterResults() {{ values = backup; }};
                else {
                    List<T> filteredItems = new LinkedList<>();
                    for (T item : collection)
                        if (item.toString().toLowerCase().contains(currentText.toString().toLowerCase()))
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
    public int getItemCount() { return collection.size(); }

    /**
     * Сохраняет ссылку на коллекцию или восстанавливает её исходное состояние.
     */
    private void setCollectionState() {
        if (originalBackup) {
            backup = collection;
            originalBackup = false;
        }
        else collection = backup;
    }

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

    private void removeItem(int position) {
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

    public Iterator<T> iterator()
        { return Collections.unmodifiableList(collection).iterator(); }
}
