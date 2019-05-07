package android.coursework.protest.Creation;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Отвечает за возможность удаления добавленных вариантов ответа свайпом.
 */
class SwipeToDeleteAnswers extends ItemTouchHelper.SimpleCallback{

    private AnswersRecyclerAdapter adapter;

    public SwipeToDeleteAnswers(AnswersRecyclerAdapter adapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.removeItem(position);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target) {
        return false;
    }
}
