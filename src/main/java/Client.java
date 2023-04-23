import java.io.*;
import java.net.Socket;

public class Client {
    public static boolean exit;

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket clientSocket = new Socket(Settings.hostFromFile("settings.txt"),
                Integer.parseInt(Settings.portNumberFromFile("settings.txt")));
        Thread sending = new Thread(clientSendMessage(clientSocket));
        Thread getting = new Thread(clientGetMessage(clientSocket));
        sending.start();
        getting.start();
        sending.join();
        getting.join();
    }

    public static Runnable clientSendMessage(Socket clientSocket) {
        return () -> {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                final String name = reader.readLine();
                out.println(name);
                if (name.equals("/exit")) {
                    exit = true;
                }
                while (!clientSocket.isClosed()) {
                    String textOut = reader.readLine();
                    if (textOut != null) {
                        out.println(textOut);
                        if (textOut.equals("/exit")) {
                            exit = true;
                        }
                        if (exit) {
                            out.close();
                            clientSocket.close();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        };
    }

    public static Runnable clientGetMessage(Socket clientSocket) {
        return () -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String textFromServer = in.readLine();
                System.out.println(textFromServer);
                while (!clientSocket.isClosed()) {
                    String textFromServer2 = in.readLine();
                    System.out.println(textFromServer2);
                    if (exit) {
                        in.close();
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        };
    }
}
