import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ServerTest {

    @BeforeAll
    public static void started() {
        System.out.println("Test started");
    }

    @AfterAll
    public static void finished() {
        System.out.println("Test completed");
    }

    @Test
    public void testConnection() {
        String message = "Test message";
        File file = new File("./file.log");
        long stockSize = file.length();
        Server.log(message);
        long msgLogSize = file.length();
        Assertions.assertTrue(msgLogSize>stockSize);
    }
}
