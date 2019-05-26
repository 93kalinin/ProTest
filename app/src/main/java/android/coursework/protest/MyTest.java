package android.coursework.protest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/*
 * Публичный конструктор без параметров и геттеры для каждого поля необходимы для хранения
 * экземпляров этого класса в FirestoreDB.
 */
class MyTest implements Serializable {

    private Map<String, Map<String, Boolean>> questions;
    private Date creationTime;
    private boolean isPrivate;
    private int accessKey;
    private String title;
    private String description;
    private ArrayList<String> tags;
    private String authorNickname;
    private String authorId;

    public MyTest() {}

    MyTest(Map<String, Map<String, Boolean>> questions, Date creationTime,
            boolean isPrivate, int accessKey, String title, String description,
            ArrayList<String> tags, String authorNickname, String authorId) {
        this.questions = questions;
        this.creationTime = creationTime;
        this.isPrivate = isPrivate;
        this.accessKey = accessKey;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.authorNickname = authorNickname;
        this.authorId = authorId;
    }

    @Override
    public String toString() { return title; }

    public Map<String, Map<String, Boolean>> getQuestions() { return questions; }
    public Date getCreationTime() { return creationTime; }
    public boolean getIsPrivate() { return isPrivate; }
    public int getAccessKey() { return accessKey; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ArrayList<String> getTags() { return tags; }
    public String getAuthorNickname() { return authorNickname; }
    public String getAuthorId() { return authorId; }
}
