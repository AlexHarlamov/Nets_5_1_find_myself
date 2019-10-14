import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Client {

    private final Map<InetAddress, Long> CurrentCopies;
    private int sendTimeout;
    private Thread st;
    private Thread lt;

    Client(String group, int port, int _sendTimeout) throws IOException {

        sendTimeout = _sendTimeout;

        CurrentCopies = Collections.synchronizedMap(new HashMap<>());

        MulticastSocket ms = new MulticastSocket(port);
        InetAddress groupIP = InetAddress.getByName(group);

        ms.joinGroup(groupIP);

        st = new Thread(new SendThread(port, groupIP, sendTimeout, ms));
        lt = new Thread(new ListenThread(groupIP,CurrentCopies, ms));

    }

    void doWork() {

        st.start();
        lt.start();

        int prev_count = 0;
        while (true) {

            int new_count = CurrentCopies.size();

            if (prev_count != new_count) {
                PrintCopies();
                prev_count = new_count;
            }

            updateStatus();

            try {
                Thread.sleep(sendTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateStatus() {
        synchronized (CurrentCopies) {
            HashMap<InetAddress, Long> tmpMap = new HashMap<>(CurrentCopies);

            for (InetAddress e : tmpMap.keySet()) {
                if (System.currentTimeMillis() - CurrentCopies.get(e) > sendTimeout * 7) {
                    CurrentCopies.remove(e);
                }
            }
        }
    }

    private void PrintCopies() {
        synchronized (CurrentCopies) {
            for ( InetAddress e : CurrentCopies.keySet() ) {
                System.out.println(e + " is alive. Last message " + (System.currentTimeMillis() - CurrentCopies.get(e)) + " ms ago");
            }
        }
    }
}
