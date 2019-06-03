package android.coursework.protest;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * Публичный конструктор без параметров и геттеры для каждого поля необходимы для хранения
 * экземпляров этого класса в FirestoreDB.
 */
class MyTest implements Serializable {

    ArrayList<Question> questions;
    Date creationTime;
    boolean isPrivate;
    boolean hideResult;
    boolean isApproved;
    int accessKey;
    String title;
    String description;
    ArrayList<String> tags;
    String authorNickname;
    String authorId;
    String testId;

    public MyTest() { }

    @Override
    public String toString() { return title; }

    MyTest(ArrayList<Question> questions, Date creationTime, boolean isPrivate, boolean hideResult,
           int accessKey, String title, String description, ArrayList<String> tags,
           String authorNickname, String authorId) {
        this.questions = questions;
        this.creationTime = creationTime;
        this.isPrivate = isPrivate;
        this.hideResult = hideResult;
        this.accessKey = accessKey;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.authorNickname = authorNickname;
        this.authorId = authorId;
    }

    public ArrayList<Question> getQuestions() { return questions; }
    public Date getCreationTime() { return creationTime; }
    public boolean getIsPrivate() { return isPrivate; }
    public boolean getHideResult() { return hideResult; }
    public boolean getIsApproved() { return isApproved; }
    public int getAccessKey() { return accessKey; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ArrayList<String> getTags() { return tags; }
    public String getAuthorNickname() { return authorNickname; }
    public String getAuthorId() { return authorId; }
    public String getTestId() { return testId; }

    /**
     * Вопрос и варианты ответа. Поле sufficient хранит число верных ответов, достаточных для зачетного
     * ответа на вопрос. Например, вопрос имеет 8 вариантов ответа, среди которых 4 верны, но может
     * быть достаточно выбора одного любого из 4-х верных вариантов, чтобы ответ считался верным.
     */
    static class Question implements Serializable {

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
        }

        String question;
        int sufficient;
        ArrayList<Answer> answers;

        public Question() { }

        Question(String question, ArrayList<Answer> answers, int sufficient) {
            this.question = question;
            this.answers = answers;
            this.sufficient = sufficient;
        }

        @Override
        public int hashCode() { return answers.hashCode(); }

        public String getQuestion() { return question; }
        public int getSufficient() { return sufficient; }
        public ArrayList<Answer> getAnswers() { return answers; }
    }

    static class TestResult {

        String testId, testTitle, testeeId, testeeName, completionPercentage, timeSpent;

        public TestResult() { }

        TestResult(String testId, String testTitle, String testeeId, String testeeName,
                   String completionPercentage, String timeSpent) {
            this.testId = testId;
            this.testTitle = testTitle;
            this.testeeId = testeeId;
            this.testeeName = testeeName;
            this.completionPercentage = completionPercentage;
            this.timeSpent = timeSpent;
        }

        public String getTestId() { return testId; }
        public String getTestTitle() { return testTitle; }
        public String getTesteeId() { return testeeId; }
        public String getTesteeName() { return testeeName; }
        public String getCompletionPercentage() { return completionPercentage; }
        public String getTimeSpent() { return timeSpent; }
    }

    /*
    Данный метод не относится к этому классу, но много где используется и его больше некуда деть.
    */
    static void printError(ConstraintLayout rootLayout, String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
