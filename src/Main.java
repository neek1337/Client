import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by neek on 06.05.16.
 */
public class Main implements Runnable {
    private static Scanner scanner;
    private static BufferedReader in;
    private static PrintWriter out;
    private static final Object monitor = new Object();
    private static boolean ready = false;

    public static void main(String[] args) throws IOException {
        System.out.println("Добро пожаловать!");
        scanner = new Scanner(System.in);
        new Thread(new Main()).start();
        String answer = scanner.next().toLowerCase();
        switch (answer) {
            case "connect":
                try {
                    connect(scanner.next(), scanner.next());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


//        String fuser, fserver;
//
//        while ((fuser = inu.readLine()) != null) {
//            out.println(fuser);
//            fserver = in.readLine();
//            System.out.println(fserver);
//            if (fuser.equalsIgnoreCase("close")) break;
//            if (fuser.equalsIgnoreCase("exit")) break;
//        }

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
        while (scanner.hasNextLine()) {
            out.println(scanner.nextLine());
        }

        out.close();
        in.close();
        fromserver.close();
    }

}
