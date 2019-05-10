package android.coursework.protest.Creation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Содержит данные о вопросе и ответах. Используется исключительно в связке с тестом MyTest.
 * Возвращает последовательность ответов Answer через итератор.
 * Полностью перекладывает проверку валидности аргументов конструктора на внешний код.
 * Конструктор по умолчанию и public поля необходимы для хранения класса в Firebase Realtime DB.
 */
public final class Question implements Iterable<Question.Answer>, Serializable {

    public static final class Answer implements Serializable {

        public boolean isCorrect;
        public String answer;

        public Answer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }

        public Answer() {}

        @Override
        public String toString() { return answer; }
    }

    public List<Answer> answers;
    public String question;

    public Question(String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public Question() {}

    @Override
    public Iterator<Answer> iterator() { return answers.iterator(); }
}
