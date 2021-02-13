package model;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;


/**
 * Raw UDP packet a simpler version DatagramPacket.
 *
 * @see DatagramPacket
 */
public class Packet {
    /**
     * The raw data From the UDP packet.
     */
    private final byte[] data;
    /**
     * The address the UDP packet sent from.
     */
    private final InetAddress address;
    /**
     * The port the UDP packet sent from.
     */
    private final int port;

    /**
     * Default constructor for a packet to create it from components.
     *
     * @param data    The raw data From the UDP packet.
     * @param address The address the UDP packet sent from.
     * @param port    The port the UDP packet sent from.
     */
    public Packet(byte[] data, InetAddress address, int port) {
        this.data = data;
        this.address = address;
        this.port = port;
    }

    /**
     * Secondary constructor to create directly from datagramPacket.
     * This constructor automatically trims unnecessary bites from the data.
     * @param datagramPacket The datagramPacket to encode into a packet.
     */
    public Packet(DatagramPacket datagramPacket) {
        this(Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()), datagramPacket.getAddress(), datagramPacket.getPort());
    }


    /**
     * Get the port number.
     *
     * @return The port the UDP packet sent from.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the IP address.
     *
     * @return The address the UDP packet sent from.
     */

    public InetAddress getAddress() {
        return address;
    }

    /**
     * Get the data.
     *
     * @return The raw data From the UDP packet.
     */
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "data=" + Arrays.toString(data) +
                ", data decoded=" + new String(data, StandardCharsets.UTF_8) +
                ", address=" + address +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return port == packet.port && Arrays.equals(data, packet.data) && Objects.equals(address, packet.address);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(address, port);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
