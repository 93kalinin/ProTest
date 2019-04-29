package android.coursework.protest;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Данные о тесте. Каждый экземпляр имеет уникальное неизменяемое сочетание ID и списка вопросов.
 * Это позволяет исключить нарушение соответствия между идентификатором и набором вопросов теста.
 * Таким образом, зная ID, всегда можно найти точно тот же самый тест, если он не был удален.
 * Название, описание и теги могут изменяться.
 */
public final class MyTest {

    public static final Pattern idRegex = Pattern.compile("\\w{4}");

    public static final class Builder {

        private LinkedHashSet<Question> questions;
        private String id;
        private boolean isPrivate;
        private String title;
        private String description;
        private HashSet<String> tags;
        private String author;

        public Builder(LinkedHashSet<Question> questions, String id)
        throws IllegalArgumentException {
            Matcher matcher = idRegex.matcher(id);
            int questionsNumber = questions.size();

            if(!matcher.matches())
                throw new IllegalArgumentException("Invalid ID");
            if(questionsNumber < 2)
                throw new IllegalArgumentException("Too few questions");
            if (questionsNumber > 128)
                throw new IllegalArgumentException("Too many questions");
            this.questions = questions;
            this.id = id;
        }

        public Builder makePrivate() {
            this.isPrivate = true;
            return this;
        }

        public Builder setTitle(String title) throws IllegalArgumentException {
            if (title.length() < 3)
                throw new IllegalArgumentException("The title is too short");
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTags(HashSet<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setAuthor(String uid) {
            this.author = uid;
            return this;
        }

        public MyTest build() throws IllegalStateException {
            if (this.title.equals(""))
                throw new IllegalStateException("Title is not set");
            if (this.author.equals(""))
                throw new IllegalStateException("Author is not set");

            MyTest test = new MyTest(questions, id, isPrivate);
            test.title = title;
            test.description = description;
            test.tags = tags;
            test.creationTime = Calendar.getInstance().getTime();
            test.author = author;
            return test;
        }
    }

    private final LinkedHashSet<Question> questions;
    public final String id;
    public final boolean isPrivate;
    private String title;
    private String description;
    private HashSet<String> tags;
    private Date creationTime;
    private String author;

    private MyTest(LinkedHashSet<Question> questions, String id, boolean isPrivate) {
        this.questions = questions;
        this.id = id;
        this.isPrivate = isPrivate;
    }

    @Override
    public int hashCode() { return questions.hashCode(); }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof MyTest))
            return false;
        MyTest that = (MyTest)other;
        return id.equals(that.id)
                && questions.equals(that.questions);
    }

    public Collection<Question> getQuestions() {
        return Collections.unmodifiableCollection(questions);
    }
    public Collection<String> getTags() {
        return Collections.unmodifiableCollection(tags);
    }
    public Date getCreationTime() {
        return new Date(creationTime.getTime());
    }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAuthor() { return author; }

    public void setTitle(String title) throws IllegalArgumentException {
        if (title.length() < 3)
            throw new IllegalArgumentException("The title is too short");
        this.title = title;
    }
    public void setDescription(String description) { this.description = description; }
    public boolean addTag(String tag) { return tags.add(tag); }
    public boolean removeTag(String tag) { return tags.remove(tag); }
}
