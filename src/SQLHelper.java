import java.sql.*;
import at.favre.lib.crypto.bcrypt.*;
import java.util.*;

public class SQLHelper {
    public static String url = "jdbc:sqlite:src/game.db";

    public static void createDataBase() {

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
    
                statement.execute("CREATE TABLE IF NOT EXISTS users (\n " 
                                        + "user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , \n "
                                        + "username TEXT NOT NULL UNIQUE, \n"
                                        + "password TEXT NOT NULL)");
                
                statement.execute("CREATE TABLE IF NOT EXISTS leader_board ( \n" 
                                        + "user_id INTEGER NOT NULL PRIMARY KEY, \n "
                                        + "games INTEGER NOT NULL DEFAULT 0, \n "
                                        + "total_score INTEGER NOT NULL DEFAULT 0, \n "
                                        + "FOREIGN KEY (user_id) REFERENCES users(user_id) \n "
                                        + ")");

                statement.execute("CREATE TABLE IF NOT EXISTS game_log ( \n"
                                        + "player1 TEXT NOT NULL, \n"
                                        + "score1 INTEGER NOT NULL, \n"
                                        + "player2 TEXT NOT NULL, \n"
                                        + "score2 INTEGER NOT NULL, \n"
                                        + "date TEXT NOT NULL\n"
                                        +")");


                } catch (SQLException sqlException) {
                    System.out.println(sqlException);
                } 
        } catch (ClassNotFoundException cnfe) {
            System.out.println(cnfe);
        }
    } 

    public static boolean getPassword(String username , String origanlPassword) {

        String getPass = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement getPassword = connection.prepareStatement(getPass)) {

                getPassword.setString(1, username);

                try ( ResultSet rs = getPassword.executeQuery();) {

                    if(rs.next()) {
                        String hashedPassword = rs.getString("password");
                        BCrypt.Result correct = BCrypt.verifyer().verify(origanlPassword.toCharArray(), hashedPassword);
                        return correct.verified;
                    }   
                } 
  
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        return false;
    }

    public static synchronized String checkUsername(String username , String password) {

        String getUsername = "SELECT * FROM users WHERE username = ? ";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement getUserStatement = connection.prepareStatement(getUsername)) {

                getUserStatement.setString(1, username);
                
                try (ResultSet rs = getUserStatement.executeQuery()) {
                    if(rs.next()) {
                        return "taken";
                    }
                } 
            
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        addPlayer(username, password);
        return "valid";
    }

    public static synchronized void addPlayer(String username , String password) {
        String pass = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        String getUser = "SELECT user_id FROM users WHERE username = ?"; 
        String addToLeader = "INSERT INTO leader_board (user_id , games , total_score) VALUES (? , ? , ?) ";
        String addToUsers = "INSERT INTO users (username , password) VALUES (? ,?)";

        try (Connection connection = DriverManager.getConnection(url);  
             PreparedStatement addUserStatement = connection.prepareStatement(addToUsers);
             PreparedStatement getUserPreparedStatement = connection.prepareStatement(getUser);
             PreparedStatement addToLeaderPreparedStatement = connection.prepareStatement(addToLeader)) {

                // add a player to the users table
                addUserStatement.setString(1, username);
                addUserStatement.setString(2, pass);
                addUserStatement.executeUpdate();

                // get the user_id from users table
                getUserPreparedStatement.setString(1, username);
                try (ResultSet rs = getUserPreparedStatement.executeQuery()) {
                    if(rs.next()){
                        int userId = rs.getInt("user_id");
                        addToLeaderPreparedStatement.setInt(1, userId);
                        addToLeaderPreparedStatement.setInt(2, 0);
                        addToLeaderPreparedStatement.setInt(3, 0);
                        addToLeaderPreparedStatement.executeUpdate();
                    }
                } 
            
        } catch (SQLException SQLException) {
            System.out.println(SQLException);
        }
    }

    public static void updateLog(String user1 , String user2 , int score1 , int score2) {
        String updateLog = "INSERT INTO game_log (player1 , score1 , player2 , score2 , date) VALUES(? , ? , ? , ? , ?)";

        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement updateLogStatement = connection.prepareStatement(updateLog)) {
                updateLogStatement.setString(1, user1);
                updateLogStatement.setInt(2, score1);
                updateLogStatement.setString(3, user2);
                updateLogStatement.setInt(4, score2);
                updateLogStatement.setString(5, new java.util.Date().toString());
                updateLogStatement.executeUpdate();
            
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
    }

    public static void updateLeader(String user , int score){
      String join = "SELECT * FROM users INNER JOIN leader_board USING (user_id) WHERE username = ?";
      String updateLeader = "UPDATE leader_board SET games = ? , total_score = ? WHERE user_id = ?";

      try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement getUserPreparedStatement = connection.prepareStatement(join);
            PreparedStatement updateLeaderPreparedStatement = connection.prepareStatement(updateLeader)) {

                getUserPreparedStatement.setString(1, user);

                try (ResultSet rs = getUserPreparedStatement.executeQuery()) {
                    if(rs.next()) {
                        int oldScore = rs.getInt("total_score");
                        int games =  rs.getInt("games");
                        int id = rs.getInt("user_id");
                        updateLeaderPreparedStatement.setInt(1, games + 1);
                        updateLeaderPreparedStatement.setInt(2, oldScore + score);
                        updateLeaderPreparedStatement.setInt(3, id);
                        updateLeaderPreparedStatement.executeUpdate();

                    }
                    
                } 
      } catch (SQLException sqlException) {
        System.out.println(sqlException);
      }
    }

    public static ArrayList<String> displayLog(String user) {
        String getLog = "SELECT * FROM game_log WHERE player1 = ? OR player2 = ?";
        ArrayList<String> log = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement getLogPreparedStatement = connection.prepareStatement(getLog);) {
            
                getLogPreparedStatement.setString(1, user);
                getLogPreparedStatement.setString(2, user);
                try (ResultSet rs = getLogPreparedStatement.executeQuery()) {
                    while (rs.next()) {
                        boolean thisPlayer = rs.getString("player1").equals(user);
                        String opponent = thisPlayer ? rs.getString("player2") : rs.getString("player1");
                        int opponentScore = thisPlayer ? rs.getInt("score2") : rs.getInt("score1");
                        int score = thisPlayer ? rs.getInt("score1") : rs.getInt("score2");
                        String date = rs.getString("date");
                        String nextGame = user + ":" + " Score: " + score + " opponent: " + opponent + " Score: " + opponentScore + " on: "  + date + "\n" ;
                        log.add(nextGame);
                    }          
                    if(log.size() != 0) return log;
                   
                } 
        } catch (SQLException SQLException) {
            System.out.println("Error getting log: " + SQLException);
        }

         log.add("No games played");
         return log;
    } 

    public static ArrayList<String> displayLeader() {
        String join = "SELECT * FROM leader_board INNER JOIN users USING (user_id) ORDER BY total_score DESC LIMIT 5";
        ArrayList<String> leader = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();) {

                try (ResultSet rs = statement.executeQuery(join)) {
                    int i = 1;
                    while (rs.next()) {
                        leader.add("NO " + i + " " + rs.getString("username") + " Scores: " + rs.getInt("total_score") + " Games: " + rs.getInt("games") + "\n");
                        i++;
                    }
                    return leader;
                } 
            
        } catch (SQLException sqlException) {
        System.out.println("ERROR getting leader_board: " + sqlException);
        }
        leader.add("no enough data");
        return leader;
    }
}
