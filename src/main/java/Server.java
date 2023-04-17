import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static volatile String clientName;
    public static Map<Integer, Socket> clientsBase = new ConcurrentHashMap<>();

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
                    Integer localPort = clientSocket.getPort();
                    clientsBase.put(localPort, clientSocket);
                    introduce(out, in, log, server);

                    while (!clientSocket.isClosed()) {
                        sendMessage(serverSocket, log);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void introduce(PrintWriter out, BufferedReader in, Log log, String
            nameServer) throws IOException, InterruptedException {
        out.println("What is ur name?");//вывод строки клиенту
        log.log(nameServer, "What is ur name?");//запись строки на сервере и в файл лог
        final String userName = in.readLine();//присвоение имени из клиента
        clientName = userName;
        out.println("Hi, " + userName + "! Have a good talking! You can write something, please.");//вывод строки клиенту
        log.log(nameServer, "Hi, " + userName + " ! Have a good talking! You can write something, please, or \"/exit\".");//запись строки на сервере и в файл лог
    }

    public static void sendMessage(ServerSocket serverSocket, Log log) throws IOException {
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String textFromClient = in.readLine();
            log.log(clientName, textFromClient);
            out.println(clientName + ", You can write something else or \"/exit\".");
            for (Socket extract : clientsBase.values()) {
                if (extract != clientSocket) {
                    try (Socket anySocket = extract;
                         PrintWriter outMsg = new PrintWriter(extract.getOutputStream(), true)) {
                        outMsg.println(clientName + "===" + textFromClient);
                    }
                }
            }
        }
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
