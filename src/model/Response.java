package model;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * The response object sent by the server.
 */
public class Response {
    /**
     * If the request was a read request.
     */
    private final boolean read;

    /**
     * The default response constructor.
     *
     * @param read If the request was a read request.
     */
    public Response(boolean read) {
        this.read = read;
    }


    /**
     * A secondary response constructor to create a response object from response bytes.
     *
     * @param bytes The byte encoded response object.
     * @throws Exception If bytes are not from a response object.
     */
    public Response(byte[] bytes) throws Exception {
        if (Arrays.equals(bytes, new byte[]{0, 3, 0, 1}) || Arrays.equals(bytes, new byte[]{0, 4, 0, 0})) {
            read = Arrays.equals(bytes, new byte[]{0, 3, 0, 1});
        } else {
            throw new Exception();
        }
    }

    /**
     * Encode response object to bytes for transmission.
     *
     * @return The byte encoded response object.
     */
    public byte[] getEncoded() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(0); //separating byte
        output.write(read ? 0x3 : 0x4); //type byte, 3 for read, 4 for write
        output.write(0); //separating byte
        output.write(read ? 0x1 : 0x0); //type byte, 1 for read, 0 for write
        return output.toByteArray();
    }

    @Override
    public String toString() {
        return "model.Response{" +
                "read=" + read +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return read == response.read;
    }

    @Override
    public int hashCode() {
        return Objects.hash(read);
    }
}
