package android.coursework.protest;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Вопрос и варианты ответа. Поле sufficient хранит число верных ответов, достаточных для зачетного
 * ответа на вопрос. Например, вопрос имеет 8 вариантов ответа, среди которых 4 верны, но может
 * быть достаточно выбора одного любого из 4-х верных вариантов, чтобы ответ считался верным.
 */
class Question implements Serializable, Iterable<Question.Answer> {

    /**
     * Хранит вариант ответа, верен ли он и выбрал ли его пользователь, проходящий тест
     */
    static class Answer implements Serializable {
        String answer;
        boolean isCorrect;
        boolean isChecked;

        Answer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }
        public Answer() { }
        public String getAnswer() { return answer; }
        public boolean getIsCorrect() { return isCorrect; }
        public boolean getIsChecked() { return isChecked; }
        boolean isCorrect() { return isCorrect == isChecked; }
    }

    String question;
    int sufficient;
    List<Answer> answers = new LinkedList<>();

    public Question() { }

    Question(String question, List<Answer> answers, int sufficient) {
        this.question = question;
        this.sufficient = sufficient;
    }

    public String getQuestion() { return question; }
    public int getSufficient() { return sufficient; }
    public List<Answer> getAnswers() { return answers; }

    @Override
    public Iterator<Answer> iterator()
        { return Collections.unmodifiableList(answers).iterator(); }
}
