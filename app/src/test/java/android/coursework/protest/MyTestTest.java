package android.coursework.protest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedHashSet;

public class MyTestTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throwsIfPassedInvalidID() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid ID");

        new MyTest.Builder(new LinkedHashSet<Question>(), "");
    }
}
