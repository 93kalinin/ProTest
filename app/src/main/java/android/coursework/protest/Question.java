package android.coursework.protest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

//TODO: допускает невалидное состояние. прикрутить дефолты
public final class Question {

    public static final class Answer {

        public final Boolean isCorrect;
        public final String answer;

        Answer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }
    }


    public final String question;
    private final LinkedHashSet<Answer> answers;

    Question(String question, LinkedHashSet<Answer> answers) {
        if (answers.size() < 2)
            throw new IllegalArgumentException("A question should have at least two answers");
        if (answers.size() > 16)
            throw new IllegalArgumentException("A question can have at most 16 answers");

        boolean noneIsCorrect = true;
        for (Answer answer : answers)
            if (answer.isCorrect)
                noneIsCorrect = false;
        if (noneIsCorrect)
            throw new IllegalArgumentException("At least one answer should be correct");

        this.question = question;
        this.answers = answers;
    }

    public Collection<Answer> getAnswers() { return Collections.unmodifiableCollection(answers); }
}
