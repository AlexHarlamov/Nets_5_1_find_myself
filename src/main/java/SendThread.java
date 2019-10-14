import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SendThread implements Runnable {

    private MulticastSocket ms;
    private DatagramPacket dp;
    private int sendTimeout;

    SendThread(int port, InetAddress groupIP, int _sendTimeout, MulticastSocket _ms) {

        ms = _ms;

        String msgAlive = "alive";
        dp = new DatagramPacket(msgAlive.getBytes(), msgAlive.length(), groupIP, port);

        sendTimeout = _sendTimeout;
        System.out.println(this.getClass().toString()+" ready\n");
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ms.send(dp);
                Thread.sleep(sendTimeout);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}