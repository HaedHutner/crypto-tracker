package dev.haedhutner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.haedhutner.modules.*;

public class CryptoTracker {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new LoggingModule(),
                new OpenApiModule(),
                new AppPropertiesModule(),
                new WebServerModule(),
                new OkHttpModule(),
                new GsonModule()
        );

        injector.getInstance(CryptoTrackerWebServer.class).start();
    }

}
