import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        String group = "224.0.0.11";

        int port = 8080;
        Client client = null;
        try {
            client = new Client(group, port, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(client).doWork();

    }
}
