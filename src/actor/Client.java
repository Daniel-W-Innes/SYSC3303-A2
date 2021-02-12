package actor;

import model.BadRequest;
import model.Request;
import model.Response;
import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Sends requests and prints the response.
 */
public class Client implements Runnable {
    /**
     * The application configuration file loader
     */
    private final Config config;
    /**
     * The client's logger
     */
    private final Logger logger;
    /**
     * The queue of requests to be sent on run
     */
    private final Queue<Request> requests;

    /**
     * Create a client to send requests from requests queue.
     *
     * @param config   the application configuration file loader.
     * @param requests the requests to send
     */
    public Client(Config config, Queue<Request> requests) {
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName());
        this.requests = requests;
    }

    /**
     * Create a client to send default requests
     *
     * @param config the application configuration
     */
    public Client(Config config) {
        this(config, getDefaultRequests());
    }


    /**
     * Generate a queue containing the default requests specified in the assignment.
     *
     * @return the request queue
     */
    public static Queue<Request> getDefaultRequests() {
        Queue<Request> requests = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            requests.add(new Request(true, "filename", "mode"));
            requests.add(new Request(false, "filename", "mode"));
        }
        //add a bad request to the end of the queue
        requests.add(new BadRequest(true, "badFilename", "badMode"));
        return requests;
    }

    /**
     * Run the client in the main thread
     *
     * @param args Unused arguments
     * @throws IOException if fails to parse the config file
     */
    public static void main(String[] args) throws IOException {
        Client client = new Client(new Config());
        client.run();
    }

    /**
     * Send requests and print response.
     */
    @Override
    public void run() {
        byte[] output = new byte[config.getIntProperty("responseMessageSize")];
        DatagramPacket datagramPacket = new DatagramPacket(output, output.length);
        byte[] input;
        //using try-with-resources to close the datagram socket.
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            for (Request request : requests) {
                //Send request
                logger.info("Request: " + request);
                input = request.getEncoded(config.getIntProperty("maxMessageSize"));
                logger.info("Request bytes: " + Arrays.toString(input));
                datagramSocket.send(new DatagramPacket(input, input.length, InetAddress.getLocalHost(), config.getIntProperty("intermediatePort")));

                //Receive response
                datagramSocket.receive(datagramPacket);
                logger.info("Response bytes: " + Arrays.toString(datagramPacket.getData()));
                logger.info("Response decoded: " + new Response(datagramPacket.getData()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
