import java.net.*;
import java.text.ParseException;
import java.util.*;
import java.io.*;

public class Game extends Thread {
    private int score1 = 0;
    private int score2 = 0;
    private Socket player1;
    private Socket player2;
    private String user1;
    private String user2;

    public Game(Socket player1 , String user1 , Socket player2 , String user2) {
        this.player1 = player1;
        this.player2 = player2;
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public void run() {
                        
        try  (PrintWriter out1 = new PrintWriter(new OutputStreamWriter(player1.getOutputStream()),true);
            PrintWriter out2 = new PrintWriter(new OutputStreamWriter(player2.getOutputStream()), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));){

                // de-serialize questions
                ArrayList<Question> questions = Helper.deserialize("src/questions.out");

                startGame(out1 , out2);
                // create five distinct random question numbers
                int[] randomQuestions = Helper.randomNumber();

                for(int i = 0 ; i < 5 ; i++) {
                    // declare point , correct answer and , correct answer index for each question

                    int correctAnswerIndex = questions.get(randomQuestions[i]).getCorrectAnswer();
                    String correctAnswer = questions.get(randomQuestions[i]).getChoices()[correctAnswerIndex-1];
                    int points = questions.get(randomQuestions[i]).getPoints();
                    
                    System.out.println(correctAnswer + " " + correctAnswerIndex + " " + points);

                    // send a question along with choices to each player
                    out1.println(questions.get(randomQuestions[i]));
                    out2.println(questions.get(randomQuestions[i]));

                    // process players answers
                    // player 1

                    int ans1 = Integer.parseInt(in1.readLine());
                    Date t1 = Helper.dateFromString(in1.readLine());

                    // player 2
                    int ans2 = Integer.parseInt(in2.readLine());
                    Date t2 = Helper.dateFromString(in2.readLine());

                    if(ans1 == correctAnswerIndex && ans2 == correctAnswerIndex){
                        if(t1.compareTo(t2) < 0) {
                            score1 += points;
                            out1.println("You Won the round!");
                            out2.println(user1 +  " Won the round! correct answer is: " + correctAnswer );
                        }
                        else {
                            score2 += points;
                            out1.println(user2 + " won the round! correct answer: " + correctAnswer);
                            out2.println("You won the round!");
                        }
                    }

                    else if (ans1 == correctAnswerIndex && ans2 != correctAnswerIndex) {
                        score1 += points;
                        out1.println("You Won the round!");
                        out2.println(user1 +  " Won the round! correct answer is: " + correctAnswer );
                    }

                    else if(ans1 != correctAnswerIndex && ans2 == correctAnswerIndex){
                        score2 += points;
                        out1.println(user2 + " won the round! correct answer: " + correctAnswer);
                        out2.println("You won the round!");
                    }
                    else {
                        out1.println("No one won corrext answer: " + correctAnswer );
                        out2.println("No one won corrext answer: " + correctAnswer );
                    }

                    Thread.sleep(2000);
                }

                displayResults(out1, out2);
                SQLHelper.updateLog(user1 , user2 , score1 , score2);
                SQLHelper.updateLeader(user1 , score1);
                SQLHelper.updateLeader(user2 , score2);
           
        } catch (InterruptedException ie){

        }catch (ParseException pe) {}
        
        catch (IOException e) {
            System.out.println(e);
        } 
    }

    public void startGame(PrintWriter out1 , PrintWriter out2) {
        out1.println("GAME STARTED: " + user1 + " VS " + user2 );
        out2.println("GAME STARTED: " + user1 + " VS " + user2 );
    }

    public void displayResults(PrintWriter out1 , PrintWriter out2){
        if(score1 > score2){
            out1.println("You won! your score: " + score1 + " oppenet score: " + score2);
            out2.println("You lost! your score: " + score2 + " opponent score: " + score1);
        }
        else if (score2 > score1) {
            out2.println("You won! your score: " + score2 + " oppenet score: " + score1);
            out1.println("You lost! your score: " + score1 + " opponent score: " + score2);
        }
        else {
            out2.println("Draw! your score: " + score2 + " opponent score: " + score1);
            out1.println("Draw! your score: " + score1 + " opponent score: " + score2);
        }
    }
}
