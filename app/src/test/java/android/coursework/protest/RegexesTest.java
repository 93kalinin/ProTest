package android.coursework.protest;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// TODO: удалить в финальной версии; нужен для тестирования правильного понимания мной регексов
public class RegexesTest {

    String properId = MyTest.idRegex.pattern();

    @Test
    public void wrongLengthOfId() {
        boolean shouldBeFalse = "1".matches(properId)
                || "".matches(properId)
                || "tooManyCharacters".matches(properId);

        assertThat(shouldBeFalse, is(false));
    }

    @Test
    public void invalidCharactersInID() {
        boolean shouldBeFalse = "1s!p".matches(properId)
                || "asd)".matches(properId)
                || " *&^".matches(properId);

        assertThat(shouldBeFalse, is(false));
    }

    @Test
    public void validIDs() {
        boolean shouldBeTrue = "1234".matches(properId)
                && "ABCD".matches(properId)
                && "1c3Z".matches(properId)
                && "asce".matches(properId);

        assertThat(shouldBeTrue, is(true));
    }
}
