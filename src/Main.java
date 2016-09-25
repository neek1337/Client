import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main implements Runnable {
    private static Scanner scanner;
    private static BufferedReader in;
    private static PrintWriter out;
    private static boolean stoped = false;

    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        //создаем отдельный поток для чтения входящих сообщений от сервера
        while (true) {
                try {
                    new Main().connect("localhost:4444", "Name");
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }

    public void run() {
        String responseLine;
        try {
            // "прослушиваем" сообщения от сервера
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером. Для выхода нажмите Enter" +
                    "");
            stoped = true;
        }
    }

    private void connect(String ip, String name) throws IOException {
        String server, port;
        if (ip.contains(":")) {
            String[] splitedIp = ip.split(":");
            if (splitedIp.length != 2) {
                return;
            }
            server = splitedIp[0];
            port = splitedIp[1];
        } else {
            // если порт не указан, то считаем, что сервер работает на "стандартном" порту
            server = ip;
            port = "4444";
        }
        Socket fromserver;
        //отлавливаем исключение, возникающее при попытке подключиться к серверу по неправильному адресу.
        try {
            fromserver = new Socket(server, Integer.parseInt(port));
        } catch (Exception e) {
            System.out.println("Неверно введен ip или порт");
            return;
        }
        //получаем InputStream и OutputSream от сервера, при этом оповещая второй поток об этом
        try {
            in = new
                    BufferedReader(new
                    InputStreamReader(fromserver.getInputStream()));
            out = new
                    PrintWriter(fromserver.getOutputStream(), true);
            out.println(name);
            String line;
            String responseLine = in.readLine();
            System.out.println(responseLine);
            // если подключение прошло успешно, создаем поток, "прослушивающий" сообщения от сервера
                Thread thread = new Thread(new Main());
                thread.start();
                while (scanner.hasNextLine()) {
                    if (stoped) {
                        return;
                    }
                    line = scanner.nextLine();
                    out.println(line);
                }

        } finally {
            out.close();
            in.close();
            fromserver.close();
        }
    }
}
