package android.coursework.protest;

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
final class MyTest implements Iterable<Question> {

    private final ArrayList<Question> questions;
    private final Date creationTime;
    public final String id;
    public final boolean isPrivate;
    public final String title;
    public final String description;
    public final String tags;
    public final String author;

    public MyTest(ArrayList<Question> questions, String id, boolean isPrivate, String title,
            String description, String tags, String author) {
        this.questions = questions;
        this.id = id;
        this.isPrivate = isPrivate;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.author = author;
        this.creationTime = Calendar.getInstance().getTime();
    }

    public Date getCreationTime() { return new Date(creationTime.getTime()); }
    public int getNumberOfQuestions() { return questions.size(); }

    @Override
    public Iterator<Question> iterator()
        { return Collections.unmodifiableList(questions).iterator(); }
}
