package actor.intermediate;

import model.Packet;
import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;

/**
 * The front half of the intermediate proxy. Takes requests from the client and adds them to the intermediate load balance to be processed by a backend.
 */
public class Frontend implements Runnable {
    /**
     * The application configuration file loader.
     */
    private final Config config;
    /**
     * The frontend's logger.
     */
    private final Logger logger;
    /**
     * The load balancer for assigning packets to a backend.
     */
    private final LoadBalancer loadBalancer;
    /**
     * The port the frontend is listening to.
     */
    private final int port;
    /**
     * If the frontend is running.
     */
    private boolean run;

    /**
     * Default constructor for the frontend.
     *
     * @param config       The application configuration file loader.
     * @param loadBalancer The intermediate load balancer.
     */
    public Frontend(Config config, LoadBalancer loadBalancer) {
        this.config = config;
        this.loadBalancer = loadBalancer;
        logger = Logger.getLogger(this.getClass().getName());
        run = true;
        port = config.getIntProperty("intermediatePort");
    }

    /**
     * Shutdown the intermediate proxy.
     */
    public void shutdown() {
        run = false;
        loadBalancer.shutdown();
    }

    /**
     * Get the port the intermediate proxy is using.
     *
     * @return The listening port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Start listening for requests.
     */
    @Override
    public void run() {
        byte[] buff;
        DatagramPacket datagramPacket;
        Packet packet;
        //using try-with-resources to close the datagram socket
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            while (run) {
                //reset buff between requests
                buff = new byte[config.getIntProperty("maxMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);

                //receive a new request
                datagramSocket.receive(datagramPacket);

                //create a simple packet from the diagram packet
                packet = new Packet(datagramPacket);
                logger.info("Request: " + packet.toString());

                //send the packet to the load balancer
                loadBalancer.add(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
