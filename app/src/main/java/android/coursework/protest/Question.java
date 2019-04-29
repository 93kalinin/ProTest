package android.coursework.protest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Вопрос для теста. Допускает задание нескольких верных ответов и указание числа ответов,
 * необходимых для полного верного ответа на вопрос (поле sufficient).
 */
public final class Question {

    public static final class Answer {

        public final Boolean isCorrect;
        public final String answer;

        Answer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this)
                return true;
            if (!(other instanceof Answer))
                return false;
            Answer that = (Answer)other;
            return that.answer.equals(this.answer);
        }

        @Override
        public int hashCode() { return this.answer.hashCode(); }
    }

    public final String question;
    public int numberOfCorrect;
    private final LinkedHashSet<Answer> answers;
    private int sufficient = 1;

    Question(String question, LinkedHashSet<Answer> answers) throws IllegalArgumentException {
        if (answers.size() < 2)
            throw new IllegalArgumentException("A question should have at least two answers");
        if (answers.size() > 16)
            throw new IllegalArgumentException("A question can have at most 16 answers");

        for (Answer answer : answers)
            if (answer.isCorrect)
                this.numberOfCorrect++;
        if (numberOfCorrect == 0)
            throw new IllegalArgumentException("At least one answer should be correct");

        this.question = question;
        this.answers = answers;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof Question))
            return false;
        Question that = (Question)other;
        return this.question.equals(that.question)
                && this.numberOfCorrect == that.numberOfCorrect
                && this.answers.equals(that.answers)
                && this.sufficient == that.sufficient;
    }

    @Override
    public int hashCode() { return Objects.hash(question, numberOfCorrect, answers, sufficient); }

    public Collection<Answer> getAnswers() { return Collections.unmodifiableCollection(answers); }
    public int getSufficient() { return sufficient; }

    public void setSufficient(int sufficient) throws IllegalArgumentException {
        if (sufficient < 1)
            throw new IllegalArgumentException("The sufficient number of answers should be" +
             "greater or equal to 1");
        if (sufficient > numberOfCorrect)
            throw new IllegalArgumentException("The sufficient number of answers cannot be " +
                "larger than the total number of correct answers");
        this.sufficient = sufficient;
    }
}
