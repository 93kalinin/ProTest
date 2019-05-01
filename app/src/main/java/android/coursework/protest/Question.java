package android.coursework.protest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Содержит данные о вопросе и ответах. Используется исключительно в связке с тестом MyTest.
 * Иммутабелен. Возвращает последовательность ответов Answer через итератор.
 * Полностью перекладывает проверку валидности аргументов конструктора на внешний код.
 *
 * Полный ответ на вопрос может требовать выбора нескольких верных вариантов. Question.sufficient
 * содержит число выбранных тестируемым верных вариантов, достаточное для полного ответа.
 * Например, из 8 вариантов 4 верные, но можно выбрать 3 или 4 из верных 4 для полного ответа.
 */
final class Question implements Iterable<Question.Answer> {

    static final class Answer {

        public final boolean isCorrect;
        public final String answer;

        Answer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }
    }

    private final ArrayList<Answer> answers;
    public final String question;
    public final int sufficient;

    Question(String question, ArrayList<Answer> answers, int sufficient) {
        this.question = question;
        this.answers = answers;
        this.sufficient = sufficient;
    }

    @Override
    public Iterator<Answer> iterator()
        { return Collections.unmodifiableList(answers).iterator(); }
}
