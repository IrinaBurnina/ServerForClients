import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Log {
    private static Log INSTANCE = null;
    private Map<String, Integer> freq = new HashMap<>();
    String fileLogName = "log.txt";

    Log() throws IOException {
        File logFile = new File(fileLogName);
        logFile.createNewFile();
    }

    public static Log getInstance() throws IOException {
        if (INSTANCE == null) {
            synchronized (Log.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Log();
                }
            }
        }
        return INSTANCE;
    }

    public void log(String userName, String msg) {
        freq.put(userName, freq.getOrDefault(userName, 0) + 1);
        String textLine = "[" + userName + "#" + freq.get(userName) + "]" +
                LocalDateTime.now() + " === " + msg;
        try (FileOutputStream fos = new FileOutputStream(fileLogName, true)) {
            byte[] bytes = textLine.getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(textLine);
    }
}
