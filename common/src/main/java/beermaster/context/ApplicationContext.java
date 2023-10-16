package beermaster.context;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ApplicationContext {
    private static final ApplicationContext context = new ApplicationContext();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private ApplicationContext() {}

    public static ApplicationContext getInstance() {
        return context;
    }

    public void shutdown() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}
