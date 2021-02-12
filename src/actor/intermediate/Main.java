package actor.intermediate;

import util.Config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Methods to group the entire intermediate proxy in one call.
 */
public class Main {
    /**
     * Run all components of the proxy. The proxy's frontend is return to allow for shutdown and to get it's listening port.
     *
     * @param config The application configuration file loader.
     * @return The proxy's frontend.
     */
    public static Frontend run(Config config) {
        Set<Backend> backends = new HashSet<>();
        //create numBackends backends
        for (int i = 0; i < config.getIntProperty("numBackends"); i++) {
            backends.add(new Backend(config));
        }
        //create the load balancer
        LoadBalancer loadBalancer = new LoadBalancer(backends);
        //create the frontend.
        Frontend frontend = new Frontend(config, loadBalancer);

        //run the backends in their own thread
        backends.forEach(backend -> new Thread(backend).start());
        //run the load balancer in a thread
        new Thread(loadBalancer).start();
        //run the frontend in a thread
        new Thread(frontend).start();
        return frontend;
    }

    /**
     * Run the intermediate proxy.
     *
     * @param args Unused arguments.
     * @throws IOException If fails to parse the config file.
     */
    public static void main(String[] args) throws IOException {
        run(new Config());
    }
}
