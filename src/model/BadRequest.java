package model;

/**
 * A bad request object.
 */
public class BadRequest extends Request {

    /**
     * Default response constructor.
     *
     * @param read     Whether to read or write from to the file
     * @param filename The file name to it from
     * @param mode     The mode/encoding of the file
     */
    public BadRequest(boolean read, String filename, String mode) {
        super(read, filename, mode);
    }

    /**
     * broken implementation of the default method.
     *
     * @return Garbage bites
     */
    public byte[] getEncoded(int maxMessageSize) {
        return new byte[]{1};
    }
}