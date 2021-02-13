package actor.intermediate;

import model.Packet;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Round robin load balancer for the intermediate proxy.
 */
public class LoadBalancer implements Runnable {
    /**
     * Blocking queue of packets to prevent the frontend from waiting on the load balancer.
     * LinkedBlockingQueue is thread-safe, "BlockingQueue implementations are thread-safe. All queuing methods achieve their effects atomically using internal locks or other forms of concurrency control."
     *
     * @see BlockingQueue
     */
    private final LinkedBlockingQueue<Packet> packets;
    /**
     * The queue of backends for the round robin. This is not a thread-safe queue.
     */
    private final Queue<Backend> backends;
    /**
     * If the load balancer is running.
     */
    private boolean run;

    /**
     * Constructor for the load balancer. There is no way to update the list of backends, after the construction, for performance and simplicity reasons.  THis is because the backends are stored in a non-thread safe queue.
     *
     * @param backends The backends in charge of handling the requests.
     */
    public LoadBalancer(Set<Backend> backends) {
        this.backends = new LinkedList<>(backends);
        packets = new LinkedBlockingQueue<>();
        run = true;
    }

    /**
     * Add a request to the queue.
     *
     * @param packet The simple udp packet.
     */
    public void add(Packet packet) {
        packets.add(packet);
    }

    /**
     * Shutdown the load balancer and all backends.
     */
    public void shutdown() {
        run = false;
        backends.forEach(Backend::shutdown);
    }

    /**
     * Run the round robin to associate packets from the queue with the backend.
     * Theoretically it is possible to modify this code to run multiple times, in separate threads, for the same load balancer, but it is questionable if that would increase performance because of the shared queue.
     */
    @Override
    public void run() {
        Backend backend;
        while (run) {
            //get a backend to handle the packet
            backend = backends.remove();
            try {
                //handle the packet
                //take is blocking if the queue is empty
                backend.handle(packets.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //add the backend back into the queue
            backends.add(backend);
        }
    }
}
