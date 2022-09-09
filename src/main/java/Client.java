import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class Client {
    private Socket socket;
    public BufferedReader in;
    private BufferedWriter out;

    private BufferedReader userInput;
    private String name;
    private String host;

    public Client() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("settings.txt")));
        int SERVER_PORT = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        this.host = "localhost";
        try {
            this.socket = new Socket(host, SERVER_PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        userInput = new BufferedReader(new InputStreamReader(System.in));
        name = enterName();
        new Read().start();
        new Write().start();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
    }

    public String enterName() {
        System.out.println("Enter your name:");

        try {
            this.name = userInput.readLine();
            out.write("Hi " + name + '\n');
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void exit() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
            log("disconnect");
            System.out.println(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ").format(Calendar.getInstance().getTime()) + name + ": disconnect" + "\n");

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void log(String message) {

        try (FileWriter writer = new FileWriter("clientFileLog.log", true)) {
            writer.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ").format(Calendar.getInstance().getTime()))
                    .append(name)
                    .append(": ")
                    .append(message)
                    .append('\n')
                    .flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class Read extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String message;
                    message = in.readLine();

                    if (message.equals("/exit")) {
                        exit();
                        break;
                    }

                    System.out.println(message);
                    log(message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class Write extends Thread {
        @Override
        public void run() {
            while (true) {
                String msg;
                try {
                    msg = userInput.readLine();
                    if (msg.equals("/exit")) {
                        exit();
                        break;
                    } else {
                        out.write(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss ").format(Calendar.getInstance().getTime()) + name + ": " + msg + "\n");
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}