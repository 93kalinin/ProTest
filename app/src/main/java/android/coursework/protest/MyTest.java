package android.coursework.protest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

/**
 * Публичный конструктор и геттеры необходимы для хранения объектов в FirestoreDB
 */
class MyTest {

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

    public Map<String, Map<String, Boolean>> getQuestions() { return questions; }
    public Date getCreationTime() { return creationTime; }
    public boolean getIsPrivate() { return isPrivate; }
    public int getAccessKey() { return accessKey; }
    public String getTitle() { if (title == null) throw new RuntimeException("FUCK!"); return title; }
    public String getDescription() { return description; }
    public ArrayList<String> getTags() { return tags; }
    public String getAuthorNickname() { return authorNickname; }
    public String getAuthorId() { return authorId; }
}
