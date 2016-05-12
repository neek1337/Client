import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main implements Runnable {
    private static Scanner scanner;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Thread thread;

    public static void main(String[] args) throws IOException {
        System.out.println("Добро пожаловать!");
        scanner = new Scanner(System.in);
        //создаем отдельный поток для чтения входящих сообщений от сервера
        while (true) {
            String[] line = scanner.nextLine().split(" ");
            switch (line[0].toLowerCase()) {
                case "connect":
                    try {
                        //проверяем кол-во введеных параметров для метода connect
                        if (line.length != 3) {
                            System.out.println("Команда connect имеет следующий вид: connect server_name[:port] UserName");
                            break;
                        }
                        new Main().connect(line[1], line[2]);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                default:
                    System.out.println("Введена неврная команда! Доступные команды: onnect server_name[:port] UserName и exit");
            }
        }
    }

    public void run() {
        String responseLine = null;
        try {
            // "прослушиваем" сообщения от сервера
            while ((responseLine = in.readLine()) != null) {
                    System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String ip, String name) throws IOException {
        String server = null, port = null;
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
        Socket fromserver = null;
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
            if (responseLine.startsWith("Количество подключенных пользователей:")) {
                thread = new Thread(new Main());
                thread.start();
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.equalsIgnoreCase("quit")) {
                        thread.stop();
                        out.println(line);
                        break;
                    }
                    out.println(line);
                }
            }
        } finally {
            out.close();
            in.close();
            fromserver.close();
        }
    }
}
