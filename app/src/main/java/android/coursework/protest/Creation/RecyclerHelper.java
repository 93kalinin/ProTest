package android.coursework.protest.Creation;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Добавляет к прокручиваемым спискам в классах MakeTest и MakeQuestion возможность удаления
 * элементов свайпом.
 */
class RecyclerHelper {

    private RecyclerHelper() {}

    static void finishSetup(RecyclerView view, LinearLayoutManager manager,
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
}
