package model;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Response object sent by server.
 */
public class Response {
    private final boolean read;

    /**
     * Default response constructor.
     *
     * @param read if the request was a read request
     */
    public Response(boolean read) {
        this.read = read;
    }


    /**
     * Construct a response object from response bytes.
     *
     * @param bytes The byte encoded response object
     * @throws Exception throws Exception if bytes are not from a response object
     */
    public Response(byte[] bytes) throws Exception {
        if (Arrays.equals(bytes, new byte[]{0, 3, 0, 1}) || Arrays.equals(bytes, new byte[]{0, 4, 0, 0})) {
            read = Arrays.equals(bytes, new byte[]{0, 3, 0, 1});
        } else {
            throw new Exception();
        }
    }

    /**
     * Encoded response object to bytes for transmission.
     *
     * @return The byte encoded response object
     */
    public byte[] getEncoded() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(0);
        output.write(read ? 0x3 : 0x4);
        output.write(0);
        output.write(read ? 0x1 : 0x0);
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
