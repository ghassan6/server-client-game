import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static final int SERVICE_PORT = 2500;
    
    public static void main(String[] args) {
        String userName = "";
        String password;
        try  {

            Socket player = new Socket(InetAddress.getLoopbackAddress(), SERVICE_PORT);
            System.out.println("Connected to the server");
            
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
                BufferedReader in = new BufferedReader(new InputStreamReader(player.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(player.getOutputStream()) , true);
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
                        while (in.readLine().equals("log") ) {
                         
                            if(!valid) System.out.println("Incorrect username or password");
                            
                            // get the user name and password from the user
                            System.out.println("press 0 to exit: ");
                            System.out.println("User name:");
                            userName = reader.readLine().trim();
                            if(userName.equals("0")) {
                                cleanUp(reader, in, out, player);
                                return;
                            }
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
                            System.out.println("press 0 to exit: ");
                            System.out.println("User name:");
                            userName = reader.readLine().trim();
                            if(userName.equals("0")) {
                                cleanUp(reader, in, out, player);
                                return;
                            }
                            System.out.println("password:");
                            password = reader.readLine();

                            if(password.equals("0")) {
                                cleanUp(reader, in, out, player);
                                return;
                            }
                            // send username/password to the server
                            out.println(userName);
                            out.println(password);

                            taken = true;
                        }
                        System.out.println("Sign-in successful");
                    }
                        // show options for the user
                        while (true) {
                        
                        System.out.println("1.. start a new game");
                        System.out.println("2.. show my results");
                        System.out.println("3.. show leader board");
                        System.out.println("4.. sign-out");

                        choice = Integer.parseInt(reader.readLine());

                        if(choice == 1) {
                            out.println("add");

                            System.out.println("finding a game...");
                            //  game thread started
                            
                            System.out.println(in.readLine());

                            for(int i = 0 ; i < 5 ; i++) {

                                // read each question alon with choices
                                for(int j = 0 ; j < 6 ; j ++) {
                                    System.out.println(in.readLine());
                                }

                                long startGame = System.currentTimeMillis();
                                boolean timedOut = false;
                                while (System.currentTimeMillis() - startGame < 15000) {
                                    if(reader.ready()) {
                                        out.println(reader.readLine());
                                        out.println(new Date());
                                        timedOut = true;
                                        break;
                                    }
                                }
                                if(!timedOut) {
                                    out.println("0");
                                    out.println(new Date());
                                }
                                // receive round result
                                System.out.println(in.readLine());
                            }
                            // receive game result
                            System.out.println(in.readLine());
                    }

                    if(choice == 2) {
                        ArrayList<String> log =  SQLHelper.displayLog(userName);
                        for(String game : log) System.out.println(game);
                    }

                    if(choice == 3) {
                        ArrayList<String> leader = SQLHelper.displayLeader();
                        for(String p : leader) System.out.println(p);
                    }

                    if(choice == 4) {
                        cleanUp(reader, in, out, player);
                        break;
                    }
                }

        } catch (ConnectException ce) {
            System.out.println("Can't connect to the server");
        } catch (IOException ioe) {System.out.println(ioe);}
    }

    public static void cleanUp(BufferedReader reader , BufferedReader in , PrintWriter out , Socket player) {
        try {
        reader.close();
        in.close();
        out.close();
        player.close();

        } catch (IOException e) {}
    }
}
