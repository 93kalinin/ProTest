package android.coursework.protest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;


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
        this.question = question;
        this.answers = answers;
    }

    public Collection<Answer> getAnswers() {
        return Collections.unmodifiableCollection(answers);
    }
}
