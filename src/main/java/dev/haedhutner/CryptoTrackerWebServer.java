package dev.haedhutner;

import dev.haedhutner.controller.AverageBuyPriceController;
import io.javalin.Javalin;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CryptoTrackerWebServer {

    @Inject
    private Javalin webServer;

    @Inject
    private Logger logger;

    @Inject
    private AverageBuyPriceController averageBuyPriceController;

    public void start() {
    }
}
