import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LogTest {
    @Test
    public void getInstanceTest() throws IOException {
        Log result = Log.getInstance();
        Assertions.assertNotEquals(result, null);
    }
}
