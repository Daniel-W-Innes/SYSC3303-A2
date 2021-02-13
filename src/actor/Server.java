package actor;

import model.Request;
import model.Response;
import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Receives requests and responses to them.
 */
public class Server implements Runnable {
    /**
     * The application configuration file loader.
     */
    public final Config config;
    /**
     * The client's logger.
     */
    private final Logger logger;

    /**
     * The default constructor for the server.
     *
     * @param config The application configuration file loader.
     */
    public Server(Config config) {
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Run the server in the main thread.
     *
     * @param args Unused arguments.
     * @throws IOException If it fails to parse the config file.
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server(new Config());
        server.run();
    }

    /**
     * Start responding to requests.
     */
    @Override
    public void run() {
        byte[] buff;
        DatagramPacket datagramPacket;
        Request request;
        Response response;
        //using try-with-resources to close the datagram socket
        try (DatagramSocket datagramSocket = new DatagramSocket(config.getIntProperty("serverPort"))) {
            while (true) {
                //reset buff between requests
                buff = new byte[config.getIntProperty("maxMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);

                //receive request
                datagramSocket.receive(datagramPacket);
                //trim unnecessary bites from the data
                buff = Arrays.copyOfRange(buff, 0, datagramPacket.getLength());
                logger.info("Request bytes: " + Arrays.toString(buff));

                //decode request
                request = Request.fromEncoded(buff);
                logger.info("Request decode: " + request);

                //encode response
                response = new Response(request.isRead());
                logger.info("Response: " + response);
                buff = response.getEncoded();
                logger.info("Response bytes: " + Arrays.toString(buff));

                //send response
                datagramSocket.send(new DatagramPacket(buff, buff.length, datagramPacket.getAddress(), datagramPacket.getPort()));
            }
        } catch (Exception e) {
            logger.severe("handling fatal exception");
            e.printStackTrace();
        }
    }
}
