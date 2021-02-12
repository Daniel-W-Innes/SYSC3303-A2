import actor.Client;
import actor.Server;
import util.Config;

import java.io.IOException;

/**
 * A main class to run all the actors
 */
public class Main {
    /**
     * A main function to run all the actors in their own thread.
     *
     * @param args Unused arguments
     * @throws IOException if fails to parse the config file
     */
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        new Thread(new Server(config)).start();
        actor.intermediate.Main.run(config);
        new Thread(new Client(config)).start();
    }
}

