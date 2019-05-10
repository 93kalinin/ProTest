package android.coursework.protest.Creation;

import android.coursework.protest.Creation.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Содержит данные о тесте. Используется для создания, прохождения и хранения тестов в БД.
 * Возвращает последовательность вопросов Question через итератор.
 * Полностью перекладывает проверку валидности аргументов конструктора на внешний код.
 * Конструктор по умолчанию и public поля необходимы для хранения класса в Firebase Realtime DB.
 */
public final class MyTest implements Iterable<Question>, Serializable {

    public ArrayList<Question> questions;
    public Date creationTime;
    public String id;
    public boolean isPrivate;
    public String accessKey;    //!!
    public String title;
    public String description;
    public String tags;
    public String authorNickname;
    public String authorId;

    public MyTest() {}

    public MyTest(ArrayList<Question> questions, String id, boolean isPrivate, String title,
            String description, String tags, String author, String authorId) {
        this.questions = questions;
        this.id = id;
        this.isPrivate = isPrivate;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.authorNickname = author;
        this.authorId = authorId;
        this.creationTime = Calendar.getInstance().getTime();
    }

    @Override
    public Iterator<Question> iterator()
        { return questions.iterator(); }
}
