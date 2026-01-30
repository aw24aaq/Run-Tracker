package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String RESULTS_FILE = "results.txt";

    /**
     * Saves text to a file with a timestamp header.
     * Behaves like the Python version:
     * - append = true → append mode
     * - append = false → overwrite mode
     */
    public static void saveResultsToFile(String text, String filepath, boolean append) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filepath, StandardCharsets.UTF_8, append))) {

            writer.write("[" + timestamp + "]\n");
            writer.write(text + "\n\n");

        } catch (IOException e) {
            // You can customise this depending on your assignment style
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Overload with default filepath and append=true
    public static void saveResultsToFile(String text) {
        saveResultsToFile(text, RESULTS_FILE, true);
    }

    // Overload with default filepath but custom append mode
    public static void saveResultsToFile(String text, boolean append) {
        saveResultsToFile(text, RESULTS_FILE, append);
    }
}
