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
     * The application configuration file loader
     */
    public final Config config;
    /**
     * The client's logger
     */
    private final Logger logger;

    /**
     * Default constructor for the server.
     *
     * @param config The application configuration file loader.
     */
    public Server(Config config) {
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Run the server in the main thread
     *
     * @param args Unused arguments
     * @throws IOException If fails to parse the config file
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server(new Config());
        server.run();
    }

    /**
     * Responds to requests.
     */
    @Override
    public void run() {
        byte[] buff;
        DatagramPacket datagramPacket;
        Request request;
        Response response;
        byte[] output;
        //using try-with-resources to close the datagram socket.
        try (DatagramSocket datagramSocket = new DatagramSocket(config.getIntProperty("serverPort"))) {
            while (true) {
                //Reset buff between requests
                buff = new byte[config.getIntProperty("maxMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);

                //Receive request
                datagramSocket.receive(datagramPacket);
                logger.info("Request bytes: " + Arrays.toString(datagramPacket.getData()));

                //Decode request
                request = Request.fromEncoded(datagramPacket.getData());
                logger.info("Request decode: " + request);

                //Encoded response
                response = new Response(request.isRead());
                logger.info("Response: " + response);
                output = response.getEncoded();
                logger.info("Response bytes: " + Arrays.toString(output));

                //Send response
                datagramSocket.send(new DatagramPacket(output, output.length, datagramPacket.getAddress(), datagramPacket.getPort()));
            }
        } catch (Exception e) {
            logger.severe("handling fatal exception");
            e.printStackTrace();
        }
    }
}
