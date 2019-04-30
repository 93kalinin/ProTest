package android.coursework.protest;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Содержит данные о тесте. Используется для создания, прохождения и хранения тестов в БД.
 * Является иммутабельным, т.к. не предполагается, что тесты после создания будут часто изменяться.
 * Получение последовательности вопросов, представленных классом Question и содержащихся в поле
 * questions, предполагается с помощью итератора.
 * Полностью перекладывает проверку валидности аргументов конструктора на класс, оперирующий
 * экземплярами MyTest.
 */
final class MyTest implements Iterable<Question> {

    private final Question[] questions;
    private final Date creationTime;
    public final String id;
    public final boolean isPrivate;
    public final String title;
    public final String description;
    public final String tags;
    public final String author;

    public MyTest(Question[] questions, String id, boolean isPrivate, String title,
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

    @Override
    public Iterator<Question> iterator() { return new QuestionsIterator(); }

    private class QuestionsIterator implements Iterator<Question> {
        private int position = 0;
        public boolean hasNext() { return  position < questions.length; }
        public Question next() { return this.hasNext() ? questions[position++] : null; }
    }
}
