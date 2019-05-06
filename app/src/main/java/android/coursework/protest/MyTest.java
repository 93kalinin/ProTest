package android.coursework.protest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Содержит данные о тесте. Используется для создания, прохождения и хранения тестов в БД.
 * Иммутабелен. Возвращает последовательность вопросов Question через итератор.
 * Полностью перекладывает проверку валидности аргументов конструктора на внешний код.
 */
public final class MyTest implements Iterable<Question>, Serializable {

    private final ArrayList<Question> questions;
    private final Date creationTime;
    public final String id;
    final boolean isPrivate;
    final String title;
    final String description;
    final String tags;
    final String authorNickname;
    final String authorId;

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

    public Date getCreationTime() { return new Date(creationTime.getTime()); }

    @Override
    public Iterator<Question> iterator()
        { return Collections.unmodifiableList(questions).iterator(); }
}
