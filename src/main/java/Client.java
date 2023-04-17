import java.io.*;
import java.net.Socket;

public class Client {
    public static boolean exit;

    public static void main(String[] args) throws InterruptedException {
        Thread sending = new Thread(clientSendMessage());
        Thread getting = new Thread(clientGetMessage());
        sending.start();
        getting.start();
    }

    public static Runnable clientSendMessage() {
        return () -> {
            String host = "netology.homework";
            try (Socket clientSocket = new Socket(host, Integer.parseInt(portNumberFromFile()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                final String name = reader.readLine();//ответ серверу
                out.println(name);
                if (name.equals("/exit")) {
                    exit = true;
                }
                while (!clientSocket.isClosed()) {
                    String answerText = reader.readLine();
                    out.println(name + " ==== " + answerText);
                    if (answerText.equals("/exit")) {
                        exit = true;
                    }
                    if (exit) {
                        out.close();
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static Runnable clientGetMessage() {
        return () -> {
            String host = "netology.homework";
            try (Socket clientSocket = new Socket(host, Integer.parseInt(portNumberFromFile()));
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
                String textFromServer = in.readLine();//приём сообщений клиентом
                System.out.println(textFromServer);//вывод сообщения клиенту
                while (!clientSocket.isClosed()) {
                    String textFromServer2 = in.readLine();
                    System.out.println(textFromServer2);
                    if (exit) {
                        in.close();
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static String portNumberFromFile() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream("settings.txt")) {
            int i;
            while ((i = fileInputStream.read()) != -1) {
                sb.append((char) i);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

}
