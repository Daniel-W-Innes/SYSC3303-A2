package actor.Intermediate;

import model.Packet;
import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;

public class Frontend implements Runnable {
    /**
     * The application configuration file loader
     */
    private final Config config;
    /**
     * The client's logger
     */
    private final Logger logger;
    private final LoadBalancer loadBalancer;
    private boolean run;
    private final int port;

    /**
     * Default constructor for the intermediate frontend.
     *
     * @param config       The application configuration file loader.
     * @param loadBalancer
     */
    public Frontend(Config config, LoadBalancer loadBalancer) {
        this.config = config;
        this.loadBalancer = loadBalancer;
        logger = Logger.getLogger(this.getClass().getName());
        run = true;
        port = config.getIntProperty("intermediatePort");
    }

    public void shutdown() {
        run = false;
        loadBalancer.shutdown();
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        byte[] buff;
        DatagramPacket datagramPacket;
        Packet packet;
        //using try-with-resources to close the datagram socket.
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            while (run) {
                //Reset buff between requests
                buff = new byte[config.getIntProperty("maxMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);
                datagramSocket.receive(datagramPacket);
                packet = new Packet(datagramPacket);
                logger.info("Request: " + packet.toString());
                loadBalancer.add(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
