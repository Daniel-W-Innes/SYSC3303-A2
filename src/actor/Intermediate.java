package actor;

import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A proxy between the server and client.
 */
public class Intermediate implements Runnable {
    /**
     * The application configuration file loader
     */
    private final Config config;
    /**
     * The client's logger
     */
    private final Logger logger;

    /**
     * Default constructor for the intermediate.
     *
     * @param config The application configuration file loader.
     */
    public Intermediate(Config config) {
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Run the intermediate in the main thread
     * @param args Unused arguments
     * @throws IOException if fails to parse the config file
     */
    public static void main(String[] args) throws IOException {
        Intermediate intermediate = new Intermediate(new Config());
        intermediate.run();
    }

    /**
     * Pass messages from client to server.
     */
    @Override
    public void run() {
        byte[] buff;
        DatagramPacket datagramPacket;
        InetAddress address;
        int port;
        //using try-with-resources to close the datagram socket.
        try (DatagramSocket datagramSocket = new DatagramSocket(config.getIntProperty("intermediatePort"))) {
            while (true) {
                //Reset buff between requests
                buff = new byte[config.getIntProperty("maxMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);

                //Receive request
                datagramSocket.receive(datagramPacket);
                logger.info("Request bytes: " + Arrays.toString(datagramPacket.getData()));

                //Record where to send response
                address = datagramPacket.getAddress();
                port = datagramPacket.getPort();

                //Pass request to server
                datagramSocket.send(new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), InetAddress.getLocalHost(), config.getIntProperty("serverPort")));

                //Clear request from buffer
                buff = new byte[1024];
                datagramPacket = new DatagramPacket(buff, buff.length);

                //Receive response
                datagramSocket.receive(datagramPacket);
                logger.info("Response bytes: " + Arrays.toString(datagramPacket.getData()));

                //Pass response to client
                datagramSocket.send(new DatagramPacket(datagramPacket.getData(), datagramPacket.getLength(), address, port));
            }
        } catch (IOException e) {
            logger.severe("handling fatal exception");
            e.printStackTrace();
        }
    }
}

