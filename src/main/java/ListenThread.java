import java.io.IOException;
import java.net.*;
import java.util.Map;

public class ListenThread  implements Runnable{

    private MulticastSocket ms;
    private InetAddress group;
    private final Map<InetAddress, Long> CurrentCopies;

    ListenThread(InetAddress _group,Map<InetAddress, Long> _CurrentCopies, MulticastSocket _ms){

        group = _group;

        CurrentCopies = _CurrentCopies;
        ms = _ms;

        System.out.println(this.getClass().toString()+" ready\n");

    }

    @Override
    public void run() {

        byte[] msg = new byte[1024];
        DatagramPacket dp = new DatagramPacket(msg, msg.length);
        while (!Thread.currentThread().isInterrupted()){
            try {
                ms.receive(dp);
                synchronized (CurrentCopies) {
                    CurrentCopies.put(dp.getAddress(), System.currentTimeMillis());
                }

            }catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println( Thread.currentThread().getName() + " finished");
        try {
            ms.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ms.close();
    }
}
