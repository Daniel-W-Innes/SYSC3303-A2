package actor.intermediate;

import model.Packet;
import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Logger;

/**
 * Backend for the intermediate proxy. Sends requests to the sever and sends the sends the response back to the client .
 */
public class Backend implements Runnable {
    /**
     * The application configuration file loader.
     */
    private final Config config;
    /**
     * The packet the backend is about to handle.
     * This a simple way to preserve one socket across the lifetime of the back end while still ensuring that socket is closed if something goes wrong.
     */
    private final SynchronousQueue<Packet> packets;
    /**
     * The backend's logger.
     */
    private final Logger logger;
    /**
     * Is the backend is running.
     */
    private boolean run;

    public Backend(Config config) {
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName());
        packets = new SynchronousQueue<>();
        run = true;
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
     * shutdown the backend. The backend does not guarantee that the packet in it's queue are processed before it shuts down. If Something is calling handle during or after the shutdown it will be blocked forever.
     */
    public void shutdown() {
        run = false;
    }

    @Override
    public void run() {
        Packet packet;
        byte[] buff;
        DatagramPacket datagramPacket;
        //using try-with-resources to close the datagram socket.
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
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

                    //received the response from the server
                    datagramSocket.receive(datagramPacket);
                    logger.info("Response bytes: " + Arrays.toString(datagramPacket.getData()));

                    //send the response to the client.
                    datagramSocket.send(new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), packet.getAddress(), packet.getPort()));
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
