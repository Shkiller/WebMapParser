import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;


public class Main {

    private static String originalUrl = "https://skillbox.ru";

    public static void main(String[] args) {
        File file = new File("WebMap.txt");
        try {
            file.createNewFile();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(file)) {
            new ForkJoinPool().invoke(new WebMap(originalUrl, "", originalUrl, new ConcurrentSkipListSet<>())).forEach(s -> {
                try {
                    writer.write(s);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

