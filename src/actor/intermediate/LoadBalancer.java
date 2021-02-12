package actor.intermediate;

import model.Packet;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class LoadBalancer implements Runnable {
    private final LinkedBlockingQueue<Packet> packetQueue;
    private final Queue<Backend> consumers;
    private boolean run;

    public LoadBalancer(Set<Backend> consumers) {
        this.consumers = new LinkedList<>(consumers);
        packetQueue = new LinkedBlockingQueue<>();
        run = true;
    }

    public void add(Packet packet) {
        packetQueue.add(packet);
    }

    public void shutdown() {
        run = false;
        consumers.forEach(Backend::shutdown);
    }

    @Override
    public void run() {
        Backend consumer;
        while (run) {
            consumer = consumers.remove();
            try {
                consumer.handle(packetQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumers.add(consumer);
        }
    }
}
