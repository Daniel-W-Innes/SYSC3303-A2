package actor.intermediate;

import model.Packet;
import util.Config;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Logger;

/**
 * Backend for the intermediate proxy. Sends requests to the sever and sends the sends the response back to the client.
 */
public class Backend implements Runnable {
    /**
     * The id for the backend used in the thread id.
     */
    private final int backendId;

    /**
     * The application configuration file loader.
     */
    private final Config config;
    /**
     * The packet the backend is about to handle.
     * This is a simple way to preserve one socket across the lifetime of the backend while still ensuring that the socket is closed if something goes wrong.
     * SynchronousQueue is thread-safe, "BlockingQueue implementations are thread-safe. All queuing methods achieve their effects atomically using internal locks or other forms of concurrency control."
     */
    private final SynchronousQueue<Packet> packets;
    /**
     * The backend's logger.
     */
    private final Logger logger;
    /**
     * If the backend is running.
     */
    private boolean run;

    /**
     * Default constructor for the backend.
     *
     * @param backendId The id for the backend used in the thread id.
     * @param config    The application configuration file loader.
     */
    public Backend(int backendId, Config config) {
        this.backendId = backendId;
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName() + backendId);
        packets = new SynchronousQueue<>();
        run = true;
    }

    /**
     * Get the id for the backend.
     *
     * @return The backend id.
     */
    public int getBackendId() {
        return backendId;
    }

    /**
     * A blocking call to offer a packet to the backend to handle.
     *
     * @param packet The packet to handle.
     */
    public void handle(Packet packet) {
        packets.offer(packet);
    }

    /**
     * Shutdown the backend. The backend does not guarantee that the packet in it's queue is processed before it shuts down. If something is calling handle during or after the shutdown it will be blocked forever.
     */
    public void shutdown() {
        run = false;
    }


    /**
     * Start assigning packets to a sever.
     */
    @Override
    public void run() {
        Packet packet;
        byte[] buff;
        DatagramPacket datagramPacket;
        //using try-with-resources to close the datagram socket.
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            //set socket to time out If the server isn't responding
            datagramSocket.setSoTimeout(10000);
            while (run) {
                //reset buff between requests
                buff = new byte[config.getIntProperty("responseMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);
                try {
                    //take the offered packet
                    //this frees the caller of handle
                    packet = packets.take();

                    //send the packet to the sever
                    datagramSocket.send(new DatagramPacket(packet.getData(), packet.getData().length, InetAddress.getLocalHost(), config.getIntProperty("serverPort")));

                    try {
                        //received the response from the server
                        datagramSocket.receive(datagramPacket);
                        logger.info("Response bytes: " + Arrays.toString(datagramPacket.getData()));

                        //send the response to the client
                        datagramSocket.send(new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), packet.getAddress(), packet.getPort()));
                    } catch (SocketTimeoutException e) {
                        logger.warning("socket timeout while waiting for response");
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
