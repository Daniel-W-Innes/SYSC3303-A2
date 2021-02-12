package model;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Request object sent by client Containing information specified in the outline.
 */
public class Request {
    private final boolean read;
    private final String filename;
    private final String mode;

    /**
     * Default response constructor.
     *
     * @param read     Whether to read or write from to the file
     * @param filename The file name to it from
     * @param mode     The mode/encoding of the file
     */
    public Request(boolean read, String filename, String mode) {
        this.read = read;
        this.filename = filename;
        this.mode = mode;
    }

    /**
     * Construct a request object from request bytes.
     * see getEncoded for the encoding schema.
     *
     * @param bytes The byte encoded request object
     * @throws Exception throws Exception if bytes are not from a request object
     */
    public static Request fromEncoded(byte[] bytes) throws Exception {
        //create a builder for parsing byte encoded requests
        Builder builder = new Builder();
        for (byte b : bytes) {
            //feed the parser the byte one by one
            builder.getState().handle(b);
        }
        return builder.build();
    }

    /**
     * Encode request object to bytes for transmission. The encode is as follows
     * |0 |1   |2 |3 - x-1 |x |x+2 - y-1|y |
     * +--+----+--+--------+--+---------+--+
     * |0 |type|0 |filename|0 |mode     |0 |
     * +--+----+--+--------+--+---------+--+
     * <p>
     * type is if the request is read(2) or write(1).
     * filename is a UTF-8 encoded string
     * mode is a UTF-8 encoded string
     * <p>
     * x is the separator between the filename and the mode and is not at a fixed position within the encoding
     * y is the termination byte and can be at any point before the maxMessageSize.
     *
     * @return The byte encoded request object
     */
    public byte[] getEncoded(int maxMessageSize) throws Exception {
        //The byteArray stream for concatenating the output.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(0); //Separating byte
        output.write(read ? 0x2 : 0x1); //type byte. 2 for read, 1 for write.
        output.writeBytes(filename.getBytes(StandardCharsets.UTF_8));//UTF-8 encoded filename
        output.write(0);//Separating byte
        output.writeBytes(mode.getBytes(StandardCharsets.UTF_8));//UTF-8 encoded mode
        output.write(0);//Termination byte
        if (output.size() > maxMessageSize) {
            throw new Exception();
        }
        return output.toByteArray();
    }

    public boolean isRead() {
        return read;
    }

    @Override
    public String toString() {
        return "model.Request{" +
                "read=" + read +
                ", filename='" + filename + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return read == request.read && Objects.equals(filename, request.filename) && Objects.equals(mode, request.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(read, filename, mode);
    }

    /**
     * Builder for request objects.
     */
    public static class Builder {
        private boolean read;
        private String filename;
        private String mode;
        private State state;

        public Builder() {
            state = new InitialState(this);
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        /**
         * Build request object from the parameters of the builder.
         *
         * @return The request object
         * @throws Exception throws Exception if not all parameters are set.
         */
        public Request build() throws Exception {
            if (filename == null || mode == null) {
                throw new Exception();
            }
            return new Request(read, filename, mode);
        }

        /**
         * Get the current state of the parser.
         *
         * @return the current state
         */
        public State getState() {
            return state;
        }

        /**
         * Set the state of the parser.
         *
         * @param state The new state
         */
        public void setState(State state) {
            this.state = state;
        }
    }
}

/**
 * Abstract state class for parser
 */
abstract class State {
    protected final Request.Builder builder;

    protected State(Request.Builder builder) {
        this.builder = builder;
    }

    /**
     * Handle an individual byte from the byte encoded request object
     *
     * @param b The byte to decode
     * @throws Exception throws Exception if byte Does not align with the format of a byte encoded request object
     */
    public abstract void handle(byte b) throws Exception;
}

/**
 * The initial of the person.
 */
class InitialState extends State {
    public InitialState(Request.Builder builder) {
        super(builder);
    }

    @Override
    public void handle(byte b) throws Exception {
        //if the first byte is not 0 throws Exception
        if (b != 0) {
            throw new Exception();
        } else {
            builder.setState(new TypeState(builder)
            );
        }
    }
}

/**
 * Decode if the request is a read or write.
 */
class TypeState extends State {
    protected TypeState(Request.Builder builder) {
        super(builder);
    }

    @Override
    public void handle(byte b) throws Exception {
        //if the type byte is not a 1 or 2 throws Exception
        if (b != 1 && b != 2) {
            throw new Exception();
        }
        //set the read parameter in the builder
        builder.setRead(b == 2);
        //set the next state to decode filename
        builder.setState(new FilenameState(builder));
    }
}

/**
 * Abstract class for parsing a string ending a 0 byte.
 */
abstract class StringState extends State {
    private final ByteArrayOutputStream output;

    protected StringState(Request.Builder builder) {
        super(builder);
        output = new ByteArrayOutputStream();
    }

    /**
     * Move to next state and set parameter in builder
     *
     * @param output The string parsed by the StringState
     */
    abstract void nextState(String output);

    @Override
    public void handle(byte b) {
        if (b == 0) {
            //pass bytes to concrete implementation
            nextState(output.toString());
        } else {
            //save byte
            output.write(b);
        }
    }
}

/**
 * Decode filename.
 */
class FilenameState extends StringState {
    protected FilenameState(Request.Builder builder) {
        super(builder);
    }

    @Override
    void nextState(String output) {
        //set the filename parameter in the builder
        builder.setFilename(output);
        //set the next state to decode mode
        builder.setState(new ModeState(builder));
    }
}

/**
 * Decode mode.
 */
class ModeState extends StringState {
    protected ModeState(Request.Builder builder) {
        super(builder);
    }

    @Override
    void nextState(String output) {
        //set the mode parameter in the builder
        builder.setMode(output);
        //set the next state to end
        builder.setState(new EndState(builder));
    }
}

/**
 * Parsing done ignoring all remaining pipe.
 */
class EndState extends State {
    protected EndState(Request.Builder builder) {
        super(builder);
    }

    @Override
    public void handle(byte b) {
        //Do nothing with byte
    }
}

