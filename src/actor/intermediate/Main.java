package actor.intermediate;

import util.Config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static Frontend run(Config config) {
        Set<Backend> backends = new HashSet<>();
        for (int i = 0; i < config.getIntProperty("numBackends"); i++) {
            backends.add(new Backend(config));
        }
        LoadBalancer loadBalancer = new LoadBalancer(backends);
        Frontend frontend = new Frontend(config, loadBalancer);

        backends.forEach(backend -> new Thread(backend).start());
        new Thread(loadBalancer).start();
        new Thread(frontend).start();
        return frontend;
    }

    public static void main(String[] args) throws IOException {
        run(new Config());
    }
}
