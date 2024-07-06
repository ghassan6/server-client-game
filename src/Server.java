import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static final int SERVICE_PORT = 2500;
    public static Stack<Socket> connectedPlayers = new Stack<>();
    public static Stack<String> users = new Stack<>();

    public static void main(String[] args) {
        SQLHelper.createDataBase();

        try (ServerSocket server = new ServerSocket(SERVICE_PORT)) {
            System.out.println("Conneted on port: " + SERVICE_PORT);
            // listen to requests from players(clients)
            while (true) {
                Socket nextPlayer = server.accept();
                System.out.println("Accepted FROM: " + nextPlayer.getInetAddress() + " PORT: " + nextPlayer.getPort());
                Entry serve = new Entry(nextPlayer);
                serve.start();
            }
        } catch (BindException be) {
            System.out.println("Can't bind to port:" + SERVICE_PORT);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static void startNewGame(Socket player , String userNAme) {
        connectedPlayers.push(player);
        users.push(userNAme);
        if(player.isClosed()) connectedPlayers.pop();

        while (true) {
            if(connectedPlayers.size() == 2) {
                Socket player1 = connectedPlayers.pop();
                String user1 = users.pop();
                Socket player2 = connectedPlayers.pop();
                String user2 = users.pop();
                Game game = new Game(player1 , user1 , player2 , user2);
                game.start();
            }
          break;
        }
    }
}
