import java.io.*;
import java.net.*;

public class Server {
    public static final int SERVICE_PORT = 2500;

    public static void main(String[] args) {
        SQLHelper.createDataBase();

        try (ServerSocket server = new ServerSocket(SERVICE_PORT)) {
            
            // listen to requests from players(clients)
            while (true) {
                Socket nextPlayer = server.accept();
                Entry serve = new Entry(nextPlayer);
                serve.start();


            }
        } catch (BindException be) {
            System.out.println("Can't bind to port:" + SERVICE_PORT);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
