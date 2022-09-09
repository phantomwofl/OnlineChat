import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    static final int numberOfThreads = 64;
    public static LinkedList<Socket> connections = new LinkedList<>();
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("settings.txt")));
        int SERVER_PORT = Integer.valueOf(properties.getProperty("SERVER_PORT"));

        try (final var serverSocket = new ServerSocket(SERVER_PORT)) {

            ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
            log("Server start");

            while (true) {

                try {

                    var socket = serverSocket.accept();
                    connections.add(socket);
                    pool.submit(() -> newConnection(socket));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void newConnection(Socket socket) {
        try {
            log("New user connection");

            final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String msg = in.readLine();
            out.write(msg + "\n");
            out.flush();
            log(msg);

            while (true) {
                String message = in.readLine();
                log(message);

                if (message.equals("/exit")) {
                    socket.close();
                    in.close();
                    out.close();
                    break;
                }

                for (Socket user : connections) {
                    if (!socket.equals(user)) {
                        var outUser = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
                        outUser.write(message + "\n");
                        outUser.flush();
                    }
                }
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    protected static void log (String message) {
        try (FileWriter writer = new FileWriter("file.log", true)) {
            writer.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ").format(Calendar.getInstance().getTime()))
                    .append(message)
                    .append('\n')
                    .flush();
            System.out.println(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
