import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
public class Helper {
    public static ArrayList deserialize(String path) {
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(path))) {
            try {
                return (ArrayList) oin.readObject();
            } catch (ClassCastException | ClassNotFoundException cce) {
                System.out.println(cce);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Can't find the file");
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        return new ArrayList<>();
    } 
    
    public static int[] randomNumber() {
        Set<Integer> set = new HashSet<>();
        Random random = new Random();

        while (set.size() < 5) {
            set.add(random.nextInt(28));
        }

        return set.stream().mapToInt(Integer::intValue).toArray();
    }

    public static Date dateFromString(String date) throws java.text.ParseException  {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy");
        return sdf.parse(date); 
    }
}
