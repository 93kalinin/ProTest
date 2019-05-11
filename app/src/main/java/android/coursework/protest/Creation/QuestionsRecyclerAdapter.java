package android.coursework.protest.Creation;

import android.coursework.protest.R;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Отвечает за соответствие прокручиваемого списка с вопросами и структуры даннх,
 * эти вопросы содержащей (поле questions). Управляет добавлением, удалением и восстановлением
 * вопросов к тесту посредством пользовательского интерфейса.
 */
class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView questionText;

        ViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.card_text);
        }
    }

    private final List<Question> questions;
    private static final int MAX_QUESTIONS_AMOUNT = 128;
    private final ConstraintLayout rootLayout;
    private Question lastDeleted;
    private int lastPosition;

    QuestionsRecyclerAdapter(ConstraintLayout rootLayout) {
        this.rootLayout = rootLayout;
        this.questions = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.generic_card, viewGroup, false);
        return new QuestionsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String question = questions.get(position).question;
        viewHolder.questionText.setText(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    void addQuestion(Question question) {
        if (questions.size() >= MAX_QUESTIONS_AMOUNT)
            showSnackbar("Достигнуто предельно допустимое число вопросов");
        questions.add(question);
        notifyDataSetChanged();
    }

    void removeItem(int position) {
        lastDeleted = questions.get(position);
        lastPosition = position;
        questions.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void restoreItem() {
        if (lastDeleted == null  ||  questions.size() >= MAX_QUESTIONS_AMOUNT) {
            showSnackbar("Не удалось восстановить элемент");
            return;
        }
        questions.add(lastPosition, lastDeleted);
        notifyItemInserted(lastPosition);
        lastDeleted = null;
    }

    List<Question> getQuestions() { return questions; }

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(rootLayout, "Вариант ответа удален",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("отмена", v -> restoreItem());
        snackbar.show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_SHORT)
                .show();
    }
}

