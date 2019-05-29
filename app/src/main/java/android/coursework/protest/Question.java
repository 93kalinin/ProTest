package android.coursework.protest;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Вопрос и варианты ответа. Поле sufficient хранит число верных ответов, достаточных для зачетного
 * ответа на вопрос. Например, вопрос имеет 8 вариантов ответа, среди которых 4 верны, но может
 * быть достаточно выбора одного любого из 4-х верных вариантов, чтобы ответ считался верным.
 */
class Question implements Serializable {

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

        @Override
        public int hashCode() { return Objects.hash(answer, isCorrect, isChecked); }

        public Answer() { }
        public String getAnswer() { return answer; }
        public boolean getIsCorrect() { return isCorrect; }
        public boolean getIsChecked() { return isChecked; }
        boolean isCorrect() { return isCorrect == isChecked; }
    }

    String question;
    int sufficient;
    List<Answer> answers;

    public Question() { }

    Question(String question, List<Answer> answers, int sufficient) {
        this.question = question;
        this.answers = answers;
        this.sufficient = sufficient;
    }

    @Override
    public int hashCode() { return answers.hashCode(); }

    public String getQuestion() { return question; }
    public int getSufficient() { return sufficient; }
    public List<Answer> getAnswers() { return answers; }
}
