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

import static android.coursework.protest.Creation.Question.Answer;

/**
 * Отвечает за соответствие прокручиваемого списка с вариантами ответа и структуры даннх,
 * эти варианты содержащей (поле answers). Управляет добавлением, удалением и восстановлением
 * вариантов ответа посредством пользовательского интерфейса.
 */
class AnswersRecyclerAdapter extends RecyclerView.Adapter<AnswersRecyclerAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView answerText;

        ViewHolder(View itemView) {
            super(itemView);
            answerText = itemView.findViewById(R.id.answer_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            Answer answer = answers.get(itemPosition);

            if (answer.isCorrect) view.setBackgroundResource(R.drawable.gray_line);
            else view.setBackgroundResource(R.drawable.line_for_selected_items);
            answer.isCorrect = !answer.isCorrect;
        }
    }

    private final List<Answer> answers = new LinkedList<>();
    private static final int MAX_ANSWERS_AMOUNT = 16;
    private final ConstraintLayout rootLayout;
    private Answer lastDeleted;
    private int lastPosition;

    AnswersRecyclerAdapter(ConstraintLayout rootLayout) {
        this.rootLayout = rootLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.answer_preview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String answer = answers.get(position).answer;
        viewHolder.answerText.setText(answer);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    void add(String answer) {
        if (answers.size() >= MAX_ANSWERS_AMOUNT)
            showSnackbar("Достигнуто предельно допустимое число ответов");
        answers.add(new Answer(answer, false));
        notifyDataSetChanged();
    }

    void removeItem(int position) {
        lastDeleted = answers.get(position);
        lastPosition = position;
        answers.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    void restoreItem() {
        if (lastDeleted == null  ||  answers.size() >= MAX_ANSWERS_AMOUNT) {
            showSnackbar("Не удалось восстановить элемент");
            return;
        }
        answers.add(lastPosition, lastDeleted);
        notifyItemInserted(lastPosition);
        lastDeleted = null;
    }

    List<Answer> getAnswers() { return answers; }

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(rootLayout, "Вариант ответа удален",
                Snackbar.LENGTH_SHORT);
        snackbar.setAction("отмена", v -> restoreItem());
        snackbar.show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
