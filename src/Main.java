import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by neek on 06.05.16.
 */
public class Main implements Runnable {
    private static Scanner scanner;
    private static BufferedReader in;
    private static PrintWriter out;
    private static final Object monitor = new Object();
    private static boolean ready = false;
    private static Thread thread;

    public static void main(String[] args) throws IOException {
        System.out.println("Добро пожаловать!");
        scanner = new Scanner(System.in);
        while (true) {
            thread = new Thread(new Main());
            thread.start();
            String answer = scanner.next().toLowerCase();
            switch (answer) {
                case "connect":
                    try {
                        connect(scanner.next(), scanner.next());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "exit":
                    thread.stop();
                    return;
                default:
                    System.out.println("Введена неврная команда! Доступные команды: onnect server_name[:port] UserName и exit");
            }
        }

    }

    public void run() {
        synchronized (monitor) {
            try {
                while (!ready) {
                    monitor.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String responseLine;


        try {
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void connect(String ip, String name) throws IOException {
        String server = null, port = null;
        if (ip.contains(":")) {
            String[] splitedIp = ip.split(":");
            if (splitedIp.length != 2) {
                return;
            }
            server = splitedIp[0];
            port = splitedIp[1];
        } else {
            server = ip;
            port = "4444";
        }
        Socket fromserver = null;

        fromserver = new Socket(server, Integer.parseInt(port));
        synchronized (monitor) {
            in = new
                    BufferedReader(new
                    InputStreamReader(fromserver.getInputStream()));
            out = new
                    PrintWriter(fromserver.getOutputStream(), true);
            ready = true;
            monitor.notifyAll();
        }
        out.println(name);
        String response;
        while (scanner.hasNextLine()) {
            response = scanner.nextLine();
            out.println(response);
            if (response.equalsIgnoreCase("quit")) {
                thread.stop();
                ready = false;
                break;
            }
        }

        out.close();
        in.close();
        fromserver.close();
    }

}
