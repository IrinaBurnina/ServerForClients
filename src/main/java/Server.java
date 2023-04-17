import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static volatile String clientName;
    static List<Thread> threadList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //завели файл логирования
        Log log = Log.getInstance();
        //завели файл настроек
        String server = "Сервер ";
        int port = 8089;
        File settings = new File("settings.txt");
        try {
            settings.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //записали информацию о номере порта подключения в файл настроек
        writeToSettings(port);
        //сообщение о запуске сервера
        log.log(server, "server is started");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
                    // Thread serverThread = new Thread(clientSocket, threadList);
                    introduce(out, in, log, server);
                    while (!clientSocket.isClosed()) {
                        getMessage(out, in, log);
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    public static void introduce(PrintWriter out, BufferedReader in, Log log, String
            nameServer) throws IOException, InterruptedException {
        out.println("What is ur name?");//вывод строки клиенту
        log.log(nameServer, "What is ur name?");//запись строки на сервере и в файл лог
        final String userName = in.readLine();//присвоение строки из клиента
        clientName = userName;
        out.println("Hi, " + userName + "! Have a good talking! You can write something, please.");//вывод строки клиенту
        log.log(nameServer, "Hi, " + userName + " ! Have a good talking! You can write something, please, or \"/exit\".");//запись строки на сервере и в файл лог
    }

    public static void getMessage(PrintWriter out, BufferedReader in, Log log) throws IOException {
        String text = in.readLine();
        log.log(clientName, text);
        out.println(clientName + ", You can write something else or \"/exit\".");
    }

    public static void writeToSettings(int port) {
        try (FileOutputStream fos = new FileOutputStream("settings.txt")) {
            String portNumber = String.valueOf(port);
            byte[] bytes = portNumber.getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
