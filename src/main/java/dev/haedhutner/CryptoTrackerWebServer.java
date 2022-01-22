package dev.haedhutner;

import dev.haedhutner.config.WebServerConfig;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CryptoTrackerWebServer {

    @Inject
    private WebServerConfig webServerConfig;

    @Inject
    private Logger logger;

    public void start() {
        webServerConfig.createServer();
    }
}
