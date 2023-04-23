import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {
    @Test
    public void hostFromFile() {
        String fileName = "settings.txt";
        String host = "Localhost";
        Settings.host = host;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = (Settings.port + " " + Settings.host).getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        String result = Settings.hostFromFile(fileName);
        Assertions.assertEquals(host, result);
    }

    @Test
    public void portNumberFromFileTest() {
        String fileName = "settings.txt";
        int portNumber = 1323;
        Settings.port = portNumber;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = String.valueOf(portNumber).getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        String result = Settings.portNumberFromFile(fileName);
        assertEquals(String.valueOf(portNumber), result);
    }

    @Test
    public void settingsFromFileTest() {
        String[] arrayString = new String[2];
        String fileName = "settings.txt";
        Settings.port = 1323;
        Settings.host = "localhost";
        arrayString[0] = String.valueOf(Settings.port);
        arrayString[1] = Settings.host;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            int portNumber = 1323;
            String host = "localhost";
            byte[] bytes = (portNumber + " " + host).getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        String[] result = Settings.settingsFromFile(fileName);
        Assertions.assertArrayEquals(result, arrayString);
    }
}
