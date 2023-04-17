import java.io.*;
import java.net.Socket;

public class Client {
    public static boolean exit;

    public static void main(String[] args) throws InterruptedException {
        Thread client1 = new Thread(clientActs());
        Thread client2 = new Thread(clientActs());
        client1.start();
        client2.start();
    }

    public static Runnable clientActs() {
        return () -> {
            String host = "netology.homework";
            try (Socket clientSocket = new Socket(host, Integer.parseInt(portNumberFromFile()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                answer(out, in, reader);
                while (true) {
                    answer(out, in, reader);
                    if (exit) {
                        in.close();
                        out.close();
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
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

    public static void answer(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException, InterruptedException {
        String textFromServer = in.readLine();
        System.out.println(textFromServer);
        String answerR = reader.readLine();
        out.println(answerR);
        if (answerR.equals("/exit")) {
            exit = true;
        }
    }
}
