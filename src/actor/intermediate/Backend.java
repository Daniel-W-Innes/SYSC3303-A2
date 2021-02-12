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

public class Backend implements Runnable {
    /**
     * The application configuration file loader
     */
    private final Config config;
    private final SynchronousQueue<Packet> packets;
    private boolean run;
    /**
     * The client's logger
     */
    private final Logger logger;

    public Backend(Config config) {
        this.config = config;
        logger = Logger.getLogger(this.getClass().getName());
        packets = new SynchronousQueue<>();
        run = true;
    }


    public void handle(Packet packet) {
        packets.offer(packet);
    }

    public void shutdown() {
        run = false;
    }

    @Override
    public void run() {
        Packet packet;
        byte[] buff;
        DatagramPacket datagramPacket;
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            while (run) {
                //Reset buff between requests
                buff = new byte[config.getIntProperty("responseMessageSize")];
                datagramPacket = new DatagramPacket(buff, buff.length);
                try {
                    packet = packets.take();
                    datagramSocket.send(new DatagramPacket(packet.getData(), packet.getData().length, InetAddress.getLocalHost(), config.getIntProperty("serverPort")));
                    datagramSocket.receive(datagramPacket);
                    logger.info("Response bytes: " + Arrays.toString(datagramPacket.getData()));
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
