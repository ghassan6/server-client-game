import java.util.*;
import java.io.*;
import java.net.*;


public class Client {
    public static final int SERVICE_PORT = 2500;
    
    public static void main(String[] args) {
        String userName = "";
        String password;
        try (Socket player = new Socket(InetAddress.getLocalHost(), SERVICE_PORT)  ) {

            System.out.println("Connected to the server");
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
                 BufferedReader in = new BufferedReader(new InputStreamReader(player.getInputStream()));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(player.getOutputStream()) , true);) {

                    // show sign-in/log-in options to the user
                    System.out.println("1.. Log in");
                    System.out.println("2.. Sign up");

                    int choice = Integer.parseInt(reader.readLine());

                    // for log in operation check if the username and password matches the data in the database
                    if(choice == 1) {

                        // send log to the server , enter the log-in block
                        out.println("log");

                        // continue to ask and check user's credentials
                        boolean valid = true;
                        while (in.readLine().equals("log")) {
                         
                            if(!valid) System.out.println("Incorrect user name or password");
                            
                            // get the user name and password from the user
                            System.out.println("User name:");
                            userName = reader.readLine();
                            System.out.println("Password: ");
                            password = reader.readLine();

                            out.println(userName);
                            out.println(password);

                            valid = false;

                        }
                        
                        System.out.println("Log-in successful");

                    }

                    if(choice == 2) {
                        out.println("sign");
                        boolean taken = false;
                        while (in.readLine().equals("sign")) {

                            if(taken) System.out.println("user name is taken");
                            System.out.println("User name:");
                            userName = reader.readLine();
                            System.out.println("password:");
                            password = reader.readLine();

                            // send username/password to the server
                            out.println(userName);
                            out.println(password);

                            taken = true;

                        }

                        System.out.println("Sign-in successful");
                    }
            
            } 


        } catch (ConnectException ce) {
            System.out.println("Can't connect to the server");
        } catch (IOException ioe) {System.out.println(ioe);}
        
    }
}
