package android.coursework.protest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/*
 * Публичный конструктор без параметров и геттеры для каждого поля необходимы для хранения
 * экземпляров этого класса в FirestoreDB.
 */
class MyTest implements Serializable {

    LinkedList<Question> questions;
    Date creationTime;
    boolean isPrivate;
    boolean hideResult;
    int accessKey;
    String title;
    String description;
    ArrayList<String> tags;
    String authorNickname;
    String authorId;
    String testId;

    public MyTest() { }

    MyTest(LinkedList<Question> questions, Date creationTime, boolean isPrivate, boolean hideResult,
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

    @Override
    public String toString() { return title; }

    public LinkedList<Question> getQuestions() { return questions; }
    public Date getCreationTime() { return creationTime; }
    public boolean getIsPrivate() { return isPrivate; }
    public boolean getHideResult() { return hideResult; }
    public int getAccessKey() { return accessKey; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ArrayList<String> getTags() { return tags; }
    public String getAuthorNickname() { return authorNickname; }
    public String getAuthorId() { return authorId; }
    public String getTestId() { return testId; }
}
