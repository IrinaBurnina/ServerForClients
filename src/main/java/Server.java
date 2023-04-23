import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static String clientName;
    public static String textFromClient;
    public static Map<Socket, String> clientsBase = new ConcurrentHashMap<>();
    static Log log;

    static {
        try {
            log = Log.getInstance();
        } catch (IOException e) {
            System.out.println("Ошибка логирования" + e.getMessage());
        }
    }

    static String server = "Сервер ";

    public static void main(String[] args) throws IOException {
        Settings.writeToSettings("settings.txt");
        log.log(server, "server is started. Let's go!");
        try (ServerSocket serverSocket =
                     new ServerSocket(Integer.parseInt(Settings.portNumberFromFile("settings.txt")))) {
            while (true) {
                ExecutorService group = Executors.newFixedThreadPool(5);
                group.execute(forGroup(serverSocket));
            }
        }
    }

    public static Runnable forGroup(ServerSocket serverSocket) {
        return () -> {
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                clientName = introduce(out, in, server);
                clientsBase.put(clientSocket, clientName);
                while (!clientSocket.isClosed()) {
                    receiving(in, out, clientSocket);
                    sendMessageToAll(clientSocket);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        };
    }

    public static String introduce(PrintWriter out, BufferedReader in, String
            nameServer) throws IOException, InterruptedException {
        String whIsUName = "What is ur name?";
        out.println(whIsUName);
        log.log(nameServer, whIsUName);
        final String userName = in.readLine();
        log.log(userName, userName);
        out.println("Hi, " + userName + "! Have a good talking! You can write something, please, or \"/exit\".");//вывод строки клиенту
        log.log(nameServer, "Hi, " + userName + " ! Have a good talking! You can write something, please, or \"/exit\".");//запись строки на сервере и в файл лог
        return userName;
    }

    public static void receiving(BufferedReader in, PrintWriter out, Socket clientSocket) throws IOException {
        textFromClient = in.readLine();//клиент написал сообщение
        log.log(clientsBase.get(clientSocket), textFromClient);//сервер его вывел
        if (textFromClient.equals("/exit")) {  //если сообшение содержит выход
            System.out.println(clientsBase.get(clientSocket) + " leave chat. Bye!");  //прощаемся
            in.close();
            out.close();
        } else if (textFromClient != null) {  //если не содержит, то отправляем ответ сервера
            out.println(clientsBase.get(clientSocket) + ", You can write something else or \"/exit\".");
        }
    }

    public static void sendMessageToAll(Socket clientSocket) throws IOException {
        for (Socket extract : clientsBase.keySet()) {
            if (!extract.equals(clientSocket)) {
                try {
                    PrintWriter outMsg = new PrintWriter(extract.getOutputStream(), true);
                    outMsg.println("[" + clientsBase.get(clientSocket) + "]" + LocalDateTime.now() + " === " + textFromClient);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
