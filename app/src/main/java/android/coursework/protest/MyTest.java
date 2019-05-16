package android.coursework.protest;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Публичный конструктор и геттеры необходимы для хранения объектов в FirestoreDB
 */
class MyTest {

    private Map<String, Map<String, Boolean>> questions;
    private Timestamp creationTime;
    private boolean isPrivate;
    private int accessKey;
    private String title;
    private String description;
    private LinkedList<String> tags;
    private String authorNickname;
    private String authorId;

    public MyTest() {}

    MyTest(Map<String, Map<String, Boolean>> questions, Timestamp creationTime,
            boolean isPrivate, int accessKey, String title, String description,
            LinkedList<String> tags, String authorNickname, String authorId) {
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

    public Map<String, Map<String, Boolean>> getQuestions() { return questions; }
    public Timestamp getCreationTime() { return creationTime; }
    public boolean getIsPrivate() { return isPrivate; }
    public int getAccessKey() { return accessKey; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LinkedList<String> getTags() { return tags; }
    public String getAuthorNickname() { return authorNickname; }
    public String getAuthorId() { return authorId; }
}
