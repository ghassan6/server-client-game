
import java.net.*;
import java.io.*;


public class Entry extends Thread {
    Socket playerSocket;

    public Entry(Socket playerSocket) {
        this.playerSocket = playerSocket;
    }

    @Override
    public void run() {
        String userName;
        String password;
        // create I/o streams with the client 

        try (BufferedReader in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(playerSocket.getOutputStream()) , true)) {
            
            String choice = in.readLine();
                // for log-in operation
            if(choice.equals("log")) {
                System.out.println("log");
                out.println("log");
                
                // get username/password from the user
                boolean valid = true;
                while (valid) {

                    userName = in.readLine();
                    password = in.readLine();
    
                    if(SQLHelper.getPassword(userName , password)) {
                        valid = false;
                        out.println("valid");
                    }

                    if(valid) out.println("log");
                }
             
            }

            // for sign-up operation

            if(choice.equals("sign")) {
                System.out.println("sign");
                out.println("sign");

                boolean isRegistered = true;

                while (isRegistered) {

                    userName = in.readLine();
                    password = in.readLine();

                    if("valid".equals(SQLHelper.checkUsername(userName))) {
                        out.println("valid");
                        SQLHelper.addPlayer(userName , password);
                        isRegistered = false;
                    }

                    else {
                        out.println("sign");
                    } 
                }
            }

        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

}
