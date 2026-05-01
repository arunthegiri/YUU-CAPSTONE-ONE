import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CSVReader {
    public static void main(String[] args) {
        // Look inside the 'resources' folder automatically
        String fileName = "transactions.csv";

        try (InputStream is = CSVReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("File not found in resources: " + fileName);
                return;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}