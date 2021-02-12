package model;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Packet {
    private final byte[] data;
    private final InetAddress address;
    private final int port;

    public Packet(byte[] data, InetAddress address, int port) {
        this.data = data;
        this.address = address;
        this.port = port;
    }

    public Packet(DatagramPacket datagramPacket) {
        this(datagramPacket.getData(), datagramPacket.getAddress(), datagramPacket.getPort());
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }

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
