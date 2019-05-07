package android.coursework.protest.Creation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Содержит данные о вопросе и ответах. Используется исключительно в связке с тестом MyTest.
 * Иммутабелен. Возвращает последовательность ответов Answer через итератор.
 * Полностью перекладывает проверку валидности аргументов конструктора на внешний код.
 */
public final class Question implements Iterable<Question.Answer>, Serializable {

    public static final class Answer implements Serializable {

        private boolean isCorrect;
        public final String answer;

        public Answer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }

        public void toggle() { isCorrect = !isCorrect; }
        public boolean isCorrect() { return isCorrect; }
    }

    private final List<Answer> answers;
    final String question;

    Question(String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    @Override
    public Iterator<Answer> iterator()
        { return Collections.unmodifiableList(answers).iterator(); }
}
